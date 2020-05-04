- a. 提升Spark运行

> `spark.sql.adaptive.enabled=true`
>  spark的自适应执行,启动Adaptive Execution
>
>  `spark.dynamicAllocation.enabled=true`
>  开启动态资源分配，Spark可以根据当前作业的负载动态申请和释放资源
>
>  `spark.dynamicAllocation.maxExecutors=${numbers}`
>  开启动态资源分配后，同一时刻，最多可申请的executor个数。task较多时，可适当调大此参数，保证task能够并发执行完成，缩短作业执行时间
>
>  `spark.dynamicAllocation.minExecutors=3`
>  某一时刻executor的最小个数。平台默认设置为3，即在任何时刻，作业都会保持至少有3个及以上的executor存活，保证任务可以迅速调度
>
>  `spark.sql.shuffle.partitions`
>  JOIN或聚合等需要shuffle的操作时，设定从mapper端写出的partition个数。类似于MR中的reducer，当partition多时，产生的文件也会多
>
>  `spark.sql.adaptive.shuffle.targetPostShuffleInputSize=67108864`
>  当mapper端两个partition的数据合并后数据量小于targetPostShuffleInputSize时，Spark会将两个partition进行合并到一个reducer端进行处理。默认64m
>
>  `spark.sql.adaptive.minNumPostShufflePartitions=50`
>  当spark.sql.adaptive.enabled参数开启后，有时会导致很多分区被合并，为了防止分区过少而影响性能。设置该参数，保障至少的shuffle分区数
>
>  `spark.hadoop.mapreduce.input.fileinputformat.split.maxsize=134217728`
>  控制在ORC切分时stripe的合并处理。当几个stripe的大小大于设定值时，会合并到一个task中处理。适当调小该值以增大读ORC表的并发 【最小大小的控制参数
>
> `spark.hadoop.mapreduce.input.fileinputformat.split.minsize`】



- b. 提升Executor执行能力

> `spark.executor.memory=4g`
>  用于缓存数据、代码执行的堆内存以及JVM运行时需要的内存。设置过小容易导致OOM，而实际执行中需要的大小可以通过文件来估算
>
>  `spark.yarn.executor.memoryOverhead=1024`
>  Spark运行还需要一些堆外内存，直接向系统申请，如数据传输时的netty等
>
>  `spark.executor.cores=4`
>  单个executor上可以同时运行的task数，该参数决定了一个executor上可以并行执行几个task。几个task共享同一个executor的内存（spark.executor.memory+spark.yarn.executor.memoryOverhead）。适当提高该参数的值，可以有效增加程序的并发度，是作业执行的更快。不过同时也增加executor内存压力，容易出现OOM

- c. 其他参数

  | 参数名称                                  | 当前           | 说明/含义                                                    |
  | ----------------------------------------- | -------------- | ------------------------------------------------------------ |
  | spark.sql.autoBroadcastJoinThreshold      | 64mb           | 使用BroadcastJoin时候表的大小阈值(-1 则取消使用)             |
  | spark.sql.broadcastTimeout                | 300s           | BroadcastJoin的等待超时的时间                                |
  | spark.default.parallelism                 | 24             | 指定每个stage默认的并行task数量，处理RDD时才会起作用，对Spark SQL的无效 |
  | spark.speculation                         | true           | 执行任务的推测执行。这意味着如果一个或多个任务在一个阶段中运行缓慢，它们将被重新启动 |
  | spark.speculation.quantile                |                | 在特定阶段启用推测之前必须完成的部分任务。推荐0.75/0.95      |
  | spark.kryoserializer.buffer.max           | 64m            | Kryo串行缓冲区的最大允许大小（以MiB为单位）。它必须大于您尝试序列化的任何对象，并且必须小于2048m。如果在Kryo中收到“超出缓冲区限制”异常，请增加此值。推荐1024m |
  | spark.sql.hive.metastorePartitionPruning  | true           |                                                              |
  | spark.sql.hive.caseSensitiveInferenceMode | INFER_AND_SAVE | 不太了解，推荐使用NEVER_INFER                                |
  | spark.sql.optimizer.metadataOnly          | true           | 启用仅使用表的元数据的元数据查询优化来生成分区列，而不是表扫描 |

- d. 常见问题

> - OOM内存溢出
>
> > Spark根据 spark.executor.memory+spark.yarn.executor.memoryOverhead的值向RM申请一个容器，当executor运行时使用的内存超过这个限制时，会被yarn kill掉。失败信息为：Container killed by YARN for exceeding memory limits. XXX of YYY physical memory used. Consider boosting spark.yarn.executor.memoryOverhead。合理的调整这两个参数
>
> - 小文件数过多
>
> > 当spark执行结束后，如果生成较多的小文件可以通过hive对文件进行合并。
> >  rc/orc文件： ALTER TABLE table_name CONCATENATE  ;
> >  其他文件：指定输出文件大小并重写表(insert overwrite table _name_new select * from table_name)
>
> - spark结果与hive结果不一致
>
> > - 数据文件字段中存在特殊字符带来的错行错列，剔除特殊字符，如： regexp_replace(name,'\n|\r|\t|\r\n|\u0001', '')
> > - spark为了优化读取parquet格式文件，使用自己的解析方式读取数据。将该方式置为false`set spark.sql.hive.convertMetastoreParquet=false`
> > - hive中对于null和空值与spark的差异。已知的办法是调整hive的参数：serialization.null.format 如：`alter table table_name set serdeproperties('serialization.null.format' = '');`





作者：别停下思考
链接：https://www.jianshu.com/p/4449dce2acc7
来源：简书
著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。