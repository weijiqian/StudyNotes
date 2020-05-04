### 分析

#### spark常见的问题不外乎oom：

我们首先看一下Spark 的内存模型：
Spark在一个Executor中的内存分为三块，一块是execution内存，一块是storage内存，一块是other内存。
execution内存是执行内存，文档中说join，aggregate都在这部分内存中执行，shuffle的数据也会先缓存在这个内存中，满了再写入磁盘，能够减少IO。其实map过程也是在这个内存中执行的。

storage内存是存储broadcast，cache，persist数据的地方。

other内存是程序执行时预留给自己的内存。

OOM的问题通常出现在execution这块内存中，因为storage这块内存在存放数据满了之后，会直接丢弃内存中旧的数据，对性能有影响但是不会有OOM的问题。

#### Spark中的OOM问题不外乎以下三种情况

1. map执行中内存溢出

2. shuffle后内存溢出

3. driver内存溢出

   前两种情况发生在executor中,最后情况发生在driver中

我们针对每种情况具体分析

#### Driver heap：

Driver heap OOM的三大原因:

##### (1).用户在Driver端口生成大对象, 比如创建了一个大的集合数据结构

解决思路:
1.1. 考虑将该大对象转化成Executor端加载. 例如调用sc.textFile/sc.hadoopFile等
1.2. 如若无法避免, 自我评估该大对象占用的内存, 相应增加driver-memory的值

##### (2).从Executor端收集数据回Driver端

比如Collect. 某个Stage中Executor端发回的所有数据量不能超过spark.driver.maxResultSize，默认1g. 如果用户增加该值, 请对应增加2delta increase到Driver Memory, resultSize该值只是数据序列化之后的Size, 如果是Collect的操作会将这些数据反序列化收集, 此时真正所需内存需要膨胀2-5倍, 甚至10倍.
解决思路:
2.1. 本身不建议将大的数据从Executor端, collect回来. 建议将Driver端对collect回来的数据所做的操作, 转化成Executor端RDD操作.
2.2. 如若无法避免, 自我评collect需要的内存, 相应增加driver-memory的值

##### (3)Spark本身框架的数据消耗.

现在在Spark1.6版本之后主要由Spark UI数据消耗, 取决于作业的累计Task个数.
解决思路:
3.1. 考虑缩小大numPartitions的Stage的partition个数, 例如从HDFS load的partitions一般自动计算, 但是后续用户的操作中做了过滤等操作已经大大减少数据量, 此时可以缩小Parititions。
3.2. 通过参数spark.ui.retainedStages(默认1000)/spark.ui.retainedJobs(默认1000)控制.
3.3. 实在没法避免, 相应增加内存.

#### Executor heap:

map过程产生大量对象导致内存溢出：
数据倾斜导致内存溢出：
coalesce调用导致内存溢出：
shuffle后内存溢出：

##### (1) reduce oom？

原因：reduce task 去map端获取数据，reduce一边拉取数据一边聚合，reduce端有一块聚合内存（executor memory * 0.2）,也就是这块内存不够
解决方法：
1.增加reduce 聚合操作的内存的比例
2.增加Executor memory的大小 --executor-memory 5G
3.减少reduce task每次拉取的数据量 设置spak.reducer.maxSizeInFlight 24m, 拉取的次数就多了，因此建立连接的次数增多，有可能会连接不上（正好赶上map task端进行GC）

##### (2) shuffle file cannot find or executor lost？

解决方法：
当出现以下异常时：shuffle file cannot find，executor lost、task lost，out of memory，可以调节

##### (3) Executor的堆外内存大小

问题原因：
1.map task所运行的executor内存不足，导致executor
挂掉了，executor里面的BlockManager就挂掉了，导致ConnectionManager不能用，也就无法建立连接，从而不能拉取数据
2.executor并没有挂掉
2.1 BlockManage之间的连接失败（map task所运行的executor正在GC）
2.2建立连接成功，map task所运行的executor正在GC
3.reduce task向Driver中的MapOutputTracker获取shuffle file位置的时候出现了问题
解决方法：
1.增大Executor内存(即堆内内存) ，申请的堆外内存也会随之增加--executor-memory 5G
2.增大堆外内存 --conf spark.yarn.executor.memoryoverhead 2048M
--conf spark.executor.memoryoverhead 2048M
(默认申请的堆外内存是Executor内存的10%，真正处理大数据的时候，这里都会出现问题，导致spark作业反复崩溃，无法运行；此时就会去调节这个参数，到至少1G（1024M），甚至说2G、4G）

注:在shuffle过程中可调的参数：
```
spark.shuffle.file.buffer
默认值：32k
参数说明：该参数用于设置shuffle write task的BufferedOutputStream的buffer缓冲大小。将数据写到磁盘文件之前，会先写入buffer缓冲中，待缓冲写满之后，才会溢写到磁盘。
调优建议：如果作业可用的内存资源较为充足的话，可以适当增加这个参数的大小（比如64k），从而减少shuffle write过程中溢写磁盘文件的次数，也就可以减少磁盘IO次数，进而提升性能。在实践中发现，合理调节该参数，性能会有1%~5%的提升。
注：shuffle中有以下操作会使用到该参数：map端：spill、合并文件时
```

```
spark.reducer.maxSizeInFlight
默认值：48m
参数说明：该参数用于设置shuffle read task的buffer缓冲大小，而这个buffer缓冲决定了每次能够拉取多少数据。
调优建议：如果作业可用的内存资源较为充足的话，可以适当增加这个参数的大小（比如96m），从而减少拉取数据的次数，也就可以减少网络传输的次数，进而提升性能。在实践中发现，合理调节该参数，性能会有1%~5%的提升。
```
##### 错误：reduce oom

reduce task去map拉数据，reduce 一边拉数据一边聚合 reduce段有一块聚合内存（executor memory * 0.2）
解决办法：1、增加reduce 聚合的内存的比例 设置spark.shuffle.memoryFraction
2、 增加executor memory的大小 --executor-memory 5G
3、减少reduce task每次拉取的数据量 设置spark.reducer.maxSizeInFlight 24m
```
spark.shuffle.io.maxRetries
默认值：3
参数说明：shuffle read task从shuffle write task所在节点拉取属于自己的数据时，如果因为网络异常导致拉取失败，是会自动进行重试的。该参数就代表了可以重试的最大次数。如果在指定次数之内拉取还是没有成功，就可能会导致作业执行失败。
调优建议：对于那些包含了特别耗时的shuffle操作的作业，建议增加重试最大次数（比如60次），以避免由于JVM的full gc或者网络不稳定等因素导致的数据拉取失败。在实践中发现，对于针对超大数据量（数十亿~上百亿）的shuffle过程，调节该参数可以大幅度提升稳定性。
shuffle file not find taskScheduler不负责重试task，由DAGScheduler负责重试stage
```
```
spark.shuffle.io.retryWait
默认值：5s
参数说明：具体解释同上，该参数代表了每次重试拉取数据的等待间隔，默认是5s。
调优建议：建议加大间隔时长（比如60s），以增加shuffle操作的稳定性。
```
```
spark.shuffle.memoryFraction
默认值：0.2
参数说明：该参数代表了Executor内存中，分配给shuffle read task进行聚合操作的内存比例，默认是20%。
调优建议：在资源参数调优中讲解过这个参数。如果内存充足，而且很少使用持久化操作，建议调高这个比例，给shuffle read的聚合操作更多内存，以避免由于内存不足导致聚合过程中频繁读写磁盘。在实践中发现，合理调节该参数可以将性能提升10%左右。
```
```
spark.shuffle.manager
默认值：sort
参数说明：该参数用于设置ShuffleManager的类型。Spark 1.5以后，有三个可选项：hash、sort和tungsten-sort。HashShuffleManager是Spark 1.2以前的默认选项，但是Spark 1.2以及之后的版本默认都是SortShuffleManager了。tungsten-sort与sort类似，但是使用了tungsten计划中的堆外内存管理机制，内存使用效率更高。
调优建议：由于SortShuffleManager默认会对数据进行排序，因此如果你的业务逻辑中需要该排序机制的话，则使用默认的SortShuffleManager就可以；而如果你的业务逻辑不需要对数据进行排序，那么建议参考后面的几个参数调优，通过bypass机制或优化的HashShuffleManager来避免排序操作，同时提供较好的磁盘读写性能。这里要注意的是，tungsten-sort要慎用，因为之前发现了一些相应的bug。
```
```
spark.shuffle.sort.bypassMergeThreshold
默认值：200
参数说明：当ShuffleManager为SortShuffleManager时，如果shuffle read task的数量小于这个阈值（默认是200），则shuffle write过程中不会进行排序操作，而是直接按照未经优化的HashShuffleManager的方式去写数据，但是最后会将每个task产生的所有临时磁盘文件都合并成一个文件，并会创建单独的索引文件。
调优建议：当你使用SortShuffleManager时，如果的确不需要排序操作，那么建议将这个参数调大一些，大于shuffle read task的数量。那么此时就会自动启用bypass机制，map-side就不会进行排序了，减少了排序的性能开销。但是这种方式下，依然会产生大量的磁盘文件，因此shuffle write性能有待提高。
```
```
spark.shuffle.consolidateFiles
默认值：false
参数说明：如果使用HashShuffleManager，该参数有效。如果设置为true，那么就会开启consolidate机制，会大幅度合并shuffle write的输出文件，对于shuffle read task数量特别多的情况下，这种方法可以极大地减少磁盘IO开销，提升性能。
调优建议：如果的确不需要SortShuffleManager的排序机制，那么除了使用bypass机制，还可以尝试将spark.shffle.manager参数手动指定为hash，使用HashShuffleManager，同时开启consolidate机制。在实践中尝试过，发现其性能比开启了bypass机制的SortShuffleManager要高出10%~30%。
```
原文:https://blog.csdn.net/yisun123456/article/details/86699502