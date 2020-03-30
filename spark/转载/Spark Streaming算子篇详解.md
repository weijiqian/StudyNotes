Spark Streaming是核心Spark API的扩展，它支持对实时数据流进行可伸缩、高吞吐量和容错的流处理。数据可以从Kafka、Flume、Kinesis或TCP套接字等多个源获取，也可以使用map、reduce、join和window等高级函数表示的复杂算法进行处理。最后，可以将处理过的数据推送到文件系统、数据库和实时仪表板。事实上，您可以将Spark的机器学习和图形处理算法应用于数据流。

```
<dependency>
    <groupId>org.apache.spark</groupId>
    <artifactId>spark-streaming_2.11</artifactId>
    <version>2.4.0</version>
</dependency>
```


离散流或DStream是Spark Streaming提供的基本抽象。它表示连续的数据流，无论是从源接收的输入数据流，还是通过转换输入流生成的经过处理的数据流。在内部，DStream由一系列连续的RDDs表示，RDDs是Spark对不可变的分布式数据集的抽象(有关更多细节，请参阅Spark编程指南)。DStream中的每个RDD都包含来自某个时间间隔的数据。在内部，DStream表示为RDDs序列。
DStream中的算子分为两大类:Transformations 和Output

### Transformations on DStreams

与RDDs类似，转换允许修改来自输入DStream的数据。DStreams支持普通Spark RDD上可用的许多转换。下面是一些常见的算子。

| Transformation                   | 含义                                             |
| -------------------------------- | ------------------------------------------------ |
| map(func)                        | DStream的每个元素应用于函数func上                |
| flatMap(func)                    | 类似map,如果有嵌套列表,就会被拆开                |
| filter(func)                     | 过滤                                             |
| repartition(numPartitions)       | 分区                                             |
| union(otherStream)               | 两个数据联合                                     |
| count()                          | 每个rdd中元素的数量                              |
| reduce(func)                     | 同reduce(func)                                   |
| countByValue()                   | 计算同一个元素的数量                             |
| reduceByKey(func, [numTasks])    | 同reduceByKey(func, [numTasks])                  |
| join(otherStream, [numTasks])    | 左右以相同的key连接,只保留双方都有的             |
| cogroup(otherStream, [numTasks]) |                                                  |
| transform(func)                  | 内层是RDD,                                       |
| updateStateByKey(func)           | 对于同一个key,把新的value和旧的value累加结合起来 |
|                                  |                                                  |



## Window Operations(窗口操作)

| Transformation                                               | 含义                                                         |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| window(windowLength, slideInterval)                          | 每隔slideInterval 时间计算一次 最近windowLength 时间的数据   |
| countByWindow(windowLength, slideInterval)                   | 返回流中元素的滑动窗口计数                                   |
| reduceByWindow(func, windowLength, slideInterval)            | 每隔slideInterval 时间计算(reduce)一次 最近windowLength 时间的数据 |
| reduceByKeyAndWindow(func, windowLength, slideInterval, [numTasks]) | 每隔slideInterval 时间计算(reducebykey)一次 最近windowLength 时间的数据 |
| countByValueAndWindow(windowLength, slideInterval, [numTasks]) | 当对(K, V)对的DStream调用时，返回一个新的(K, Long)对的DStream，其中每个Key的值是它在滑动窗口中的频率 |
|                                                              |                                                              |



## Output Operations on DStreams(输出操作)

| Output Operation                    | 含义                                                         |
| ----------------------------------- | ------------------------------------------------------------ |
| print()                             | 在运行流应用程序的驱动程序节点上打印DStream中每批数据的前10个元素。这对于开发和调试非常有用。这在Python API中称为pprint()。 |
| saveAsTextFiles(prefix, [suffix])   | 将此DStream的内容保存为文本文件。每个批处理间隔的文件名是根据前缀和后缀生成的:“prefix- time_in_ms [.suffix]”。 |
| saveAsObjectFiles(prefix, [suffix]) | 将此DStream的内容保存为序列化Java对象的sequencefile。每个批处理间隔的文件名是根据前缀和后缀生成的:“prefix- time_in_ms [.suffix]”。这在Python API中是不可用的。 |
| saveAsHadoopFiles(prefix, [suffix]) | 将这个DStream的内容保存为Hadoop文件。每个批处理间隔的文件名是根据前缀和后缀生成的:“prefix- time_in_ms [.suffix]”。这在Python API中是不可用的。 |
| foreachRDD(func)                    | 对流生成的每个RDD应用函数func的最通用输出操作符。这个函数应该将每个RDD中的数据推送到外部系统，例如将RDD保存到文件中，或者通过网络将其写入数据库。请注意，函数func是在运行流应用程序的驱动程序进程中执行的，其中通常会有RDD操作，这将强制流RDDs的计算。在func中创建远程连接时可以使用foreachPartition 替换foreach操作以降低系统的总体吞吐量。 |
|                                     |                                                              |

