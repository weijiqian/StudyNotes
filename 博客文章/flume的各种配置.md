## source

- 1 avro 从网络收集数据

```
#a2
a2.sources = r2
a2.sinks= k2
a2.channels = c2

a2.sources.r2.type=avro
a2.sources.r2.bind=localhost
a2.sources.r2.port=9999

a2.sinks.k2.type = logger

a2.channels.c2.type=memory

a2.sources.r2.channels = c2
a2.sinks.k2.channel = c2
```



- 2  exec  收集文件中的内容

  ```
  a1.sources = r1
  a1.sinks = k1
  a1.channels = c1
  
  a1.sources.r1.type=exec
  a1.sources.r1.command=tail -F /home/centos/test.txt
  
  a1.sinks.k1.type=logger
  
  a1.channels.c1.type=memory
  
  a1.sources.r1.channels=c1
  a1.sinks.k1.channel=c1
  
  
  ```

  

- 3  kafka 

```
a1.sources = r1
a1.sinks = k1
a1.channels = c1

a1.sources.r1.type = org.apache.flume.source.kafka.KafkaSource
a1.sources.r1.batchSize = 5000
a1.sources.r1.batchDurationMillis = 2000
a1.sources.r1.kafka.bootstrap.servers = s202:9092
a1.sources.r1.kafka.topics = test3
a1.sources.r1.kafka.consumer.group.id = g4

a1.sinks.k1.type = logger

a1.channels.c1.type=memory

a1.sources.r1.channels = c1
a1.sinks.k1.channel = c1


```



## channel

- 1 存放在文件中,安全,性能低

```
a1.sources = r1
a1.sinks= k1
a1.channels = c1

a1.sources.r1.type=netcat
a1.sources.r1.bind=localhost
a1.sources.r1.port=8888

a1.sinks.k1.type=logger

a1.channels.c1.type = file
a1.channels.c1.checkpointDir = /home/centos/flume/fc_check
a1.channels.c1.dataDirs = /home/centos/flume/fc_data

a1.sources.r1.channels=c1
a1.sinks.k1.channel=c1

```

- 2  kafka  高性能

```
a1.sources = r1
a1.sinks = k1
a1.channels = c1

a1.sources.r1.type = avro
a1.sources.r1.bind = localhost
a1.sources.r1.port = 8888

a1.sinks.k1.type = logger

a1.channels.c1.type = org.apache.flume.channel.kafka.KafkaChannel
a1.channels.c1.kafka.bootstrap.servers = s202:9092
a1.channels.c1.kafka.topic = test3
a1.channels.c1.kafka.consumer.group.id = g6
a1.channels.c1.kafka.parseAsFlumeEvent = false
a1.channels.c1.zookeeperConnect= s202:2181

a1.sources.r1.channels = c1
a1.sinks.k1.channel = c1

```



## sink

- 1 avro 

```
#a1
a1.sources = r1
a1.sinks= k1
a1.channels = c1

a1.sources.r1.type=netcat
a1.sources.r1.bind=localhost
a1.sources.r1.port=8888

a1.sinks.k1.type = avro
a1.sinks.k1.hostname=localhost
a1.sinks.k1.port=9999

a1.channels.c1.type=memory

a1.sources.r1.channels = c1
a1.sinks.k1.channel = c1
```



- 2 hbase  数据收集到hbase中

```
a1.sources = r1
a1.channels = c1
a1.sinks = k1

a1.sources.r1.type = netcat
a1.sources.r1.bind = localhost
a1.sources.r1.port = 8888

a1.sinks.k1.type = hbase
a1.sinks.k1.table = ns1:t12
a1.sinks.k1.columnFamily = f1
a1.sinks.k1.serializer = org.apache.flume.sink.hbase.RegexHbaseEventSerializer

a1.channels.c1.type=memory

a1.sources.r1.channels = c1
a1.sinks.k1.channel = c1

```



- 3 hdfs  收集到hdfs中

```
a1.sources = r1
a1.channels = c1
a1.sinks = k1

a1.sources.r1.type = netcat
a1.sources.r1.bind = localhost
a1.sources.r1.port = 8888

a1.sinks.k1.type = hdfs
a1.sinks.k1.hdfs.path = /user/centos/flume/%y/%m/%d/%H/%M/%S
a1.sinks.k1.hdfs.filePrefix = events-
a1.sinks.k1.hdfs.round = true
a1.sinks.k1.hdfs.roundValue = 20
a1.sinks.k1.hdfs.roundUnit = second
a1.sinks.k1.hdfs.useLocalTimeStamp=true
a1.sinks.k1.hdfs.rollInterval=10
a1.sinks.k1.hdfs.rollSize=10
a1.sinks.k1.hdfs.rollCount=3

a1.channels.c1.type=memory

a1.sources.r1.channels = c1
a1.sinks.k1.channel = c1

```



- 4 kafka  

```
a1.sources = r1
a1.sinks = k1
a1.channels = c1

a1.sources.r1.type=netcat
a1.sources.r1.bind=localhost
a1.sources.r1.port=8888

a1.sinks.k1.type = org.apache.flume.sink.kafka.KafkaSink
a1.sinks.k1.kafka.topic = test3
a1.sinks.k1.kafka.bootstrap.servers = s202:9092
a1.sinks.k1.kafka.flumeBatchSize = 20
a1.sinks.k1.kafka.producer.acks = 1

a1.channels.c1.type=memory

a1.sources.r1.channels = c1
a1.sinks.k1.channel = c1


```



# 多个source 汇总

```
# Name the components on this agent
a1.sources = r1 r2 r3
a1.sinks = k1
a1.channels = c1

# Describe/configure the source
a1.sources.r1.type = exec
a1.sources.r1.command = tail -F /home/hadoop/logs/access.log
a1.sources.r1.interceptors = i1
a1.sources.r1.interceptors.i1.type = static
a1.sources.r1.interceptors.i1.key = type
a1.sources.r1.interceptors.i1.value = access

a1.sources.r2.type = exec
a1.sources.r2.command = tail -F /home/hadoop/logs/nginx.log
a1.sources.r2.interceptors = i2
a1.sources.r2.interceptors.i2.type = static
a1.sources.r2.interceptors.i2.key = type
a1.sources.r2.interceptors.i2.value = nginx

a1.sources.r3.type = exec
a1.sources.r3.command = tail -F /home/hadoop/logs/web.log
a1.sources.r3.interceptors = i3
a1.sources.r3.interceptors.i3.type = static
a1.sources.r3.interceptors.i3.key = type
a1.sources.r3.interceptors.i3.value = web

# Describe the sink
a1.sinks.k1.type = avro
a1.sinks.k1.hostname = hdp-node-03
a1.sinks.k1.port = 41414

# Use a channel which buffers events in memory
a1.channels.c1.type = memory
a1.channels.c1.capacity = 2000000
a1.channels.c1.transactionCapacity = 100000

# Bind the source and sink to the channel
a1.sources.r1.channels = c1
a1.sources.r2.channels = c1
a1.sources.r3.channels = c1
a1.sinks.k1.channel = c1
```



# flume的高可用

hdp03是主agent,hdp04是备份的.

```
指定别名

agent1.sources = r1
agent1.channels = c1
agent1.sinks = k1 k2

指定一个sink的组名

agent1.sinkgroups = g1

指定source 

agent1.sources.r1.type = exec
agent1.sources.r1.command = tail -F /home/hadoop/flume_data/access.log

定义拦截器

agent1.sources.r1.interceptors = i1 i2
agent1.sources.r1.interceptors.i1.type = static
agent1.sources.r1.interceptors.i1.key = Type
agent1.sources.r1.interceptors.i1.value = LOGIN
agent1.sources.r1.interceptors.i2.type = timestamp
#指定channnel
agent1.channels.c1.type = memory
agent1.channels.c1.capacity = 1000
agent1.channels.c1.transactionCapacity = 100

指定sink1 主agent

agent1.sinks.k1.type = avro
agent1.sinks.k1.hostname = hdp03
agent1.sinks.k1.port = 52020

指定备份的sink  

agent1.sinks.k2.type = avro
agent1.sinks.k2.hostname = hdp04
agent1.sinks.k2.port = 52020

设置组中的sink成员

agent1.sinkgroups.g1.sinks = k1 k2

设置失败自启方案   切换的方案  priority 指定优先级  越大  优先级越高  处理数据的时候 先进行处理 优先级高的agent存活 优先级低的agent不接受数据的

agent1.sinkgroups.g1.processor.type = failover # 失败方案
agent1.sinkgroups.g1.processor.priority.k1 = 10
agent1.sinkgroups.g1.processor.priority.k2 = 1
agent1.sinkgroups.g1.processor.maxpenalty = 10000

进行绑定

agent1.sources.r1.channels = c1
agent1.sinks.k1.channel = c1
agent1.sinks.k2.channel = c1




```



# 优化配置

```
#channel中最多缓存多少
a1.channels.c1.capacity = 20000
#channel一次最多吐给sink多少
a1.channels.c1.transactionCapacity = 10000
#event的活跃时间
a1.channels.c1.keep-alive = 10


#时间类型
a1.sinks.k1.hdfs.useLocalTimeStamp = true
#生成的文件不按条数生成
a1.sinks.k1.hdfs.rollCount = 0
#生成的文件按时间生成
a1.sinks.k1.hdfs.rollInterval = 30
#生成的文件按大小生成
a1.sinks.k1.hdfs.rollSize = 10485760
#批量写入 hdfs 的个数  优化
a1.sinks.k1.hdfs.batchSize = 20
#flume 操作 hdfs 的线程数（包括新建，写入等）  优化
a1.sinks.k1.hdfs.threadsPoolSize=10
#操作 hdfs 超时时间
a1.sinks.k1.hdfs.callTimeout=30000
```

