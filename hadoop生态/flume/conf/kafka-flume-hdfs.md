



![image-20200401185504167](/Users/weijiqian/Desktop/大数据/StudyNotes/image-md/image-20200401185504167.png)

```shell
## 组件
a1.sources=r1 r2
a1.channels=c1 c2
a1.sinks=k1 k2

## source1
a1.sources.r1.type = org.apache.flume.source.kafka.KafkaSource
a1.sources.r1.batchSize = 5000
a1.sources.r1.batchDurationMillis = 2000
a1.sources.r1.kafka.bootstrap.servers = hadoop102:9092,hadoop103:9092,hadoop104:9092
a1.sources.r1.kafka.topics=topic_start

## source2
a1.sources.r2.type = org.apache.flume.source.kafka.KafkaSource
a1.sources.r2.batchSize = 5000
a1.sources.r2.batchDurationMillis = 2000
a1.sources.r2.kafka.bootstrap.servers = hadoop102:9092,hadoop103:9092,hadoop104:9092
a1.sources.r2.kafka.topics=topic_event

## channel1
a1.channels.c1.type = file
a1.channels.c1.checkpointDir = /opt/module/flume/checkpoint/behavior1
a1.channels.c1.dataDirs = /opt/module/flume/data/behavior1/
a1.channels.c1.maxFileSize = 2146435071
a1.channels.c1.capacity = 1000000
a1.channels.c1.keep-alive = 6

## channel2
a1.channels.c2.type = file
a1.channels.c2.checkpointDir = /opt/module/flume/checkpoint/behavior2
a1.channels.c2.dataDirs = /opt/module/flume/data/behavior2/
a1.channels.c2.maxFileSize = 2146435071
a1.channels.c2.capacity = 1000000
a1.channels.c2.keep-alive = 6

## sink1
a1.sinks.k1.type = hdfs
a1.sinks.k1.hdfs.path = /origin_data/gmall/log/topic_start/%Y-%m-%d
a1.sinks.k1.hdfs.filePrefix = logstart-
a1.sinks.k1.hdfs.round = true
a1.sinks.k1.hdfs.roundValue = 10
a1.sinks.k1.hdfs.roundUnit = second

##sink2
a1.sinks.k2.type = hdfs
a1.sinks.k2.hdfs.path = /origin_data/gmall/log/topic_event/%Y-%m-%d
a1.sinks.k2.hdfs.filePrefix = logevent-
a1.sinks.k2.hdfs.round = true
a1.sinks.k2.hdfs.roundValue = 10
a1.sinks.k2.hdfs.roundUnit = second

## 不要产生大量小文件
a1.sinks.k1.hdfs.rollInterval = 10
a1.sinks.k1.hdfs.rollSize = 134217728
a1.sinks.k1.hdfs.rollCount = 0

a1.sinks.k2.hdfs.rollInterval = 10
a1.sinks.k2.hdfs.rollSize = 134217728
a1.sinks.k2.hdfs.rollCount = 0

## 控制输出文件是原生文件。
a1.sinks.k1.hdfs.fileType = CompressedStream 
a1.sinks.k2.hdfs.fileType = CompressedStream 

a1.sinks.k1.hdfs.codeC = lzop
a1.sinks.k2.hdfs.codeC = lzop

## 拼装
a1.sources.r1.channels = c1
a1.sinks.k1.channel= c1

a1.sources.r2.channels = c2
a1.sinks.k2.channel= c2
```

### SINKS.HDFS配置说明：

channel

- type

hdfs

- path

**写入hdfs的路径，需要包含文件系统标识，比如：hdfs://namenode/flume/webdata/**

可以使用flume提供的日期及%{host}表达式。

- filePrefix

默认值：FlumeData

写入hdfs的文件名前缀，可以使用flume提供的日期及%{host}表达式。

- fileSuffix

写入hdfs的文件名后缀，比如：.lzo .log等。

- inUsePrefix

临时文件的文件名前缀，hdfs sink会先往目标目录中写临时文件，再根据相关规则重命名成最终目标文件；

- inUseSuffix

默认值：.tmp

临时文件的文件名后缀。

- rollInterval

默认值：30

hdfs sink间隔多长将临时文件滚动成最终目标文件，单位：秒；

如果设置成0，则表示不根据时间来滚动文件；

注：滚动（roll）指的是，hdfs sink将临时文件重命名成最终目标文件，并新打开一个临时文件来写入数据；

- rollSize

默认值：1024

当临时文件达到该大小（单位：bytes）时，滚动成目标文件；

如果设置成0，则表示不根据临时文件大小来滚动文件；

- rollCount

默认值：10

当events数据达到该数量时候，将临时文件滚动成目标文件；

如果设置成0，则表示不根据events数据来滚动文件；

- idleTimeout

默认值：0 当目前被打开的临时文件在该参数指定的时间（秒）内，没有任何数据写入，则将该临时文件关闭并重命名成目标文件；

- batchSize

默认值：100

每个批次刷新到HDFS上的events数量；

- codeC

文件压缩格式，包括：gzip, bzip2, lzo, lzop, snappy

- fileType

默认值：SequenceFile

文件格式，包括：SequenceFile, DataStream,CompressedStream

当使用DataStream时候，文件不会被压缩，不需要设置hdfs.codeC;

当使用CompressedStream时候，必须设置一个正确的hdfs.codeC值；

- maxOpenFiles

默认值：5000

最大允许打开的HDFS文件数，当打开的文件数达到该值，最早打开的文件将会被关闭；

- minBlockReplicas

默认值：HDFS副本数

写入HDFS文件块的最小副本数。

该参数会影响文件的滚动配置，一般将该参数配置成1，才可以按照配置正确滚动文件。

- writeFormat

写sequence文件的格式。包含：Text, Writable（默认）

- callTimeout

默认值：10000

执行HDFS操作的超时时间（单位：毫秒）；

- threadsPoolSize

默认值：10

hdfs sink启动的操作HDFS的线程数。

- rollTimerPoolSize

默认值：1

hdfs sink启动的根据时间滚动文件的线程数。

- kerberosPrincipal

HDFS安全认证kerberos配置；

- kerberosKeytab

HDFS安全认证kerberos配置；

- proxyUser

代理用户

- round

默认值：false

是否启用时间上的”舍弃”，这里的”舍弃”，类似于”四舍五入”，后面再介绍。如果启用，则会影响除了%t的其他所有时间表达式；

- roundValue

默认值：1

时间上进行“舍弃”的值；

- roundUnit

默认值：seconds

时间上进行”舍弃”的单位，包含：second,minute,hour

 

示例：

a1.sinks.k1.hdfs.path = /flume/events/%y-%m-%d/%H%M/%S

a1.sinks.k1.hdfs.round = true

a1.sinks.k1.hdfs.roundValue = 10

a1.sinks.k1.hdfs.roundUnit = minute

当时间为2015-10-16 17:38:59时候，hdfs.path依然会被解析为：

/flume/events/20151016/17:30/00

因为设置的是舍弃10分钟内的时间，因此，该目录每10分钟新生成一个。

- timeZone

默认值：Local Time

时区。

- useLocalTimeStamp

默认值：flase

是否使用当地时间。

- closeTries

默认值：0

hdfs sink关闭文件的尝试次数；

如果设置为1，当一次关闭文件失败后，hdfs sink将不会再次尝试关闭文件，这个未关闭的文件将会一直留在那，并且是打开状态。

设置为0，当一次关闭失败后，hdfs sink会继续尝试下一次关闭，直到成功。

- retryInterval

默认值：180（秒）

hdfs sink尝试关闭文件的时间间隔，如果设置为0，表示不尝试，相当于于将hdfs.closeTries设置成1.

- serializer

默认值：TEXT

序列化类型。其他还有：avro_event或者是实现了EventSerializer.Builder的类名。

### kafka-hdfs.conf配置文件内容：（每10分钟创建一个滚动文件夹，数据文件每50M刷新一次）

```
kafka-hdfs.sources = s1
kafka-hdfs.sinks = k1
kafka-hdfs.channels = c1
 
#设置kafka源
kafka-hdfs.sources.s1.type = org.apache.flume.source.kafka.KafkaSource
#一批次写入通道的消息最大数
kafka-hdfs.sources.s1.batchSize = 10000
#kafka集群
kafka-hdfs.sources.s1.kafka.bootstrap.servers = 172.25.21.4:9099,172.25.21.5:9099,17
2.25.21.6:9099
#订阅的topics，可定义多个
kafka-hdfs.sources.s1.kafka.topics = testFace,testFace2
#消费者组id
kafka-hdfs.sources.s1.kafka.consumer.group.id=test-consumer-group
#设置sink类型
kafka-hdfs.sinks.k1.type = hdfs
#设置为hdfs目录，文件存储位置
kafka-hdfs.sinks.k1.hdfs.path = hdfs://172.25.21.4:8020/home/hcx/flume/%Y%m%d%H%M
 
#文件前缀
kafka-hdfs.sinks.k1.hdfs.filePrefix = logs-
kafka-hdfs.sinks.k1.hdfs.round = true
#开启时间上的舍弃，没10分钟创建一个文件夹
kafka-hdfs.sinks.k1.hdfs.roundValue = 10
kafka-hdfs.sinks.k1.hdfs.roundUnit = minute
#使用本地时间戳
kafka-hdfs.sinks.k1.hdfs.useLocalTimeStamp=true
kafka-hdfs.sinks.k1.hdfs.writeFormat = Text
#文件类型
kafka-hdfs.sinks.k1.hdfs.fileType = DataStream
kafka-hdfs.sinks.k1.hdfs.rollCount = 10000
kafka-hdfs.sinks.k1.hdfs.rollInterval = 600
kafka-hdfs.sinks.k1.hdfs.batchSize = 10000
kafka-hdfs.sinks.k1.hdfs.rollSize = 52428800
kafka-hdfs.sinks.k1.hdfs.threadsPoolSize = 20
 
 
kafka-hdfs.channels.c1.type = memory
kafka-hdfs.channels.c1.capacity = 1000000
kafka-hdfs.channels.c1.transactionCapacity = 10000
 
kafka-hdfs.sources.s1.channels=c1
kafka-hdfs.sinks.k1.channel =c1
```





————————————————
版权声明：本文为CSDN博主「HeCCXX」的原创文章，遵循 CC 4.0 BY-SA 版权协议，转载请附上原文出处链接及本声明。
原文链接：https://blog.csdn.net/Nonoroya_Zoro/article/details/85322027