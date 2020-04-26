Hive和MapReduce中拥有较多在特定情况下优化的特性，如何利用好相关特性，是Hive性能调优的关键。本文就介绍那些耳熟但不能详的几种Hive优化模式。

## **本地模式**

当一个MapReduce任务的数据量和计算任务很小的时候，在MapReduce框架中Map任务和Reduce任务的启动过程占用了任务执行的大部分时间，真正的逻辑处理其实占用时间很少，但是给用户的感受就是：很小的任务，同样执行较长的时间。比如对一张码表进行计算，总时间可能接近1~2分钟，这个对于用户来说，感受很差。

那么在0.7版本之后，Hive引入了本地模式，那么对于小任务的执行，Hive客户端不再需要到Yarn上申请Map任务和Reduce任务，只需要在本地进行Map和Reduce的执行，大大的加快了小任务的执行时间，通常可以把分钟级别任务的执行时间降低秒级。

参数设置：

| 参数名称                                  | 默认值    | 说明                                                         |
| :---------------------------------------- | :-------- | :----------------------------------------------------------- |
| hive.exec.mode.local.auto                 | false     | 是否开启本地模式                                             |
| hive.exec.mode.local.auto.inputbytes.max  | 134217728 | 该参数限定了Map输入的文件大小。如果是高压缩率列存文件，可适当减小此值，避免进入本地模式，一般选择默认值即可。 |
| hive.exec.mode.local.auto.input.files.max | 4         | 参数限定了Map输入的文件个数。如果是多个小文件，可适当增大此值，一般选择默认值即可。 |

实际测试中，使用本地模式之后，对于小表的计算查询能从34秒减少到2秒。

## **并行模式**

Hive的Parallel特性使得某些任务中的stage子任务以并行执行模式同时执行，相对于一直串行执行stage任务来说有效的提升资源利用率。

Parallel特性主要针对如下几种情况：

- 多个数据表关联
- 插入多个目标表
- UNION ALL

参数设置：

| 参数名称                         | 默认值 | 说明                       |
| :------------------------------- | :----- | :------------------------- |
| hive.exec.parallel               | false  | 是否开启自动转换为PARALLEL |
| hive.exec.parallel.thread.number | 8      | 最大并行度                 |

实际测试中，我们选用TDC-DS中的Q11，从对比结果看，在使用Parallel特性之后，由原来的743秒减少到600秒，在并行任务数据量较大，集群资源较充足，计算较复杂的情况下，任务执行效率提升会更加明显。

## **严格模式**

Hive提供一个严格模式，可以防止用户执行那些可能产生意想不到的影响查询。

参数设置：

| 参数名称         | 默认值                                                   | 说明               |
| :--------------- | :------------------------------------------------------- | :----------------- |
| hive.mapred.mode | hive 1.x 默认nostrict；hive 2.x 默认strict（HIVE-12413） | 设置hive的严格模式 |

通过设置hive.mapred.mode的值为strict，开启严格模式可以禁止3种类型的查询：

(1) 对于分区表，要求必须限定分区字段，换句话说就是不允许扫描所有的分区，这是因为通常所有分区的数据量都比较大，这样可以避免消耗大量资源。

(2) 对于使用order by的查询，要求必须使用limit语句，因为order by为了执行排序过程会将所有的结果数据分发到同一个reducer中进行处理，这样可以避免reducer执行过长的时间。

(3)限制笛卡尔积查询，要求两张表join时必须有on语句。

## **Uber模式**

Uber模式准确的说并不是Hive的优化特性，是Yran上针对MR小作业的优化机制，如果job任务足够小，则直接让任务串行的在MRAppMaster完成，这样整个Application只会使用一个Container（JVM重用功能），相对于分配多个Container来说执行效率要高很多。

参数设置：

| 参数名称                          | 默认值         | 说明                           |
| :-------------------------------- | :------------- | :----------------------------- |
| mapreduce.job.ubertask.enable     | false          | 是否开启小任务ubertask优化模式 |
| mapreduce.job.ubertask.maxmaps    | 9              | 设置Uber模式的最大Map数量      |
| mapreduce.job.ubertask.maxreduces | 1              | 设置Uber模式的最大Reduce数量   |
| mapreduce.job.ubertask.maxbytes   | dfs.block.size | 设置Uber模式的最大字节数       |

仅仅满足以上四个参数还不行，因为作业是在AM所在的Container中运行，Uber任务执行还应满足如下条件：

(1) Map内存设置（mapreduce.map.memory.mb）和Reduce内存设置（mapreduce.reduce.memory.mb）必须小于等于AM所在容器内存大小设置（yarn.app.mapreduce.am.resource.mb）。

(2) Map配置的vcores（mapreduce.map.cpu.vcores）个数和 Reduce配置的vcores（mapreduce.reduce.cpu.vcores）个数也必须小于等于AM所在容器vcores个数的设置（yarn.app.mapreduce.am.resource.cpu-vcores）。

上面就是今天介绍的Hive优化模式，你是不是都掌握了呢。



**往期推荐：**

[视频 | 58同城HBase平台及生态建设实践](http://mp.weixin.qq.com/s?__biz=MzUxOTU5Mjk2OA==&mid=2247485699&idx=1&sn=33ef4ec044fae960f5f2b094f1860bed&chksm=f9f60464ce818d726e93e30b592d0a23bfaa111df3843bc42d1256ec6f860038a76a90c34fef&scene=21#wechat_redirect)

[如何快速全面掌握Kafka？5000字吐血整理](http://mp.weixin.qq.com/s?__biz=MzUxOTU5Mjk2OA==&mid=2247485579&idx=1&sn=158b1c1bdc6aa3ff2a16794c6cc5abdf&chksm=f9f605ecce818cfabbaa8f07c09f9a73745fd1e7e166808845f4d14b9d8fd22acef921c0d1f7&scene=21#wechat_redirect)