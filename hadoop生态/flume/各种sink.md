[TOC]



## sink：

指定最终的采集的数据的归属地

### 1）logger(测试用)

​	将最终采集的日志 控制台打印日志中的

### 2）hdfs sink (常用)

​	将采集的数据  存储在hdfs上
eg:

```
指定当前agent a1的 sources sinks  channels 的别名

a1.sources = r1
a1.sinks = k1
a1.channels = c1

agent的数据源的

a1.sources.r1.type = exec
a1.sources.r1.command = cat  /home/hadoop/apps/apache-flume-1.8.0-bin/conf/flume-env.sh

# Describe the sink
#下沉目标
a1.sinks.k1.type = hdfs
a1.sinks.k1.channel = c1
#指定目录, flum帮做目的替换
a1.sinks.k1.hdfs.path = /flume/events/%y-%m-%d/%H%M/
#文件的命名, 前缀
a1.sinks.k1.hdfs.filePrefix = events-
 
#10 分钟就改目录（创建目录）， （这些参数影响/flume/events/%y-%m-%d/%H%M/）
a1.sinks.k1.hdfs.round = true
a1.sinks.k1.hdfs.roundValue = 10
a1.sinks.k1.hdfs.roundUnit = minute
#目录里面有文件
#------start----两个条件，只要符合其中一个就满足---
#文件滚动之前的等待时间(秒)
a1.sinks.k1.hdfs.rollInterval = 3
#文件滚动的大小限制(bytes)
a1.sinks.k1.hdfs.rollSize = 500
#写入多少个event数据后滚动文件(事件个数)
a1.sinks.k1.hdfs.rollCount = 20
#-------end-----
 
#5个事件就往里面写入
a1.sinks.k1.hdfs.batchSize = 5
 
#用本地时间格式化目录
a1.sinks.k1.hdfs.useLocalTimeStamp = true
 
#下沉后, 生成的文件类型，默认是Sequencefile，可用DataStream，则为普通文本
a1.sinks.k1.hdfs.fileType = DataStream
 

指定agent的通道

a1.channels.c1.type = memory

绑定agent的  r1   c1   k1 

a1.sources.r1.channels = c1
a1.sinks.k1.channel = c1	



```





注意：如果hadoop集群是高可用集群  请将hdfs-site.xml  core-site.xml 放在flume的conf下 
 cp /home/hadoop/apps/hadoop-2.7.6/etc/hadoop/core-site.xml /home/hadoop/apps/hadoop-2.7.6/etc/hadoop/hdfs-site.xml . 

启动：

```shell
flume-ng agent --conf conf --conf-file /home/hadoop/apps/apache-flume-1.8.0-bin/conf/test_hdfs_sink.conf --name a1
```

日志：
Creating /data/flume/test_01/FlumeData.1554704872838.tmp
	
数据默认命名：
	hdfs.filePrefix    FlumeData  指定文件前缀
	

	hdfs.rollInterval	30	默认的文件回滚的时间间隔30s
	hdfs.rollSize	1024	默认文件回滚的大小限制  1024byte 1kb
	hdfs.rollCount	10	  默认文件回滚的数据条数
	
	这3个回滚条件只要满足一个  就会进行文件回滚
	回滚： 旧文件关闭写入  形成一个新的文件  重新写入
	一般写的时候 修改的

改进：
eg:

```shell
#指定当前agent a1的 sources sinks  channels 的别名

a1.sources = r1
a1.sinks = k1
a1.channels = c1

#agent的数据源的

a1.sources.r1.type = exec
a1.sources.r1.command = cat  /home/hadoop/apps/apache-flume-1.8.0-bin/conf/flume-env.sh

#指定agent的sink的

a1.sinks.k1.type = hdfs

#指定存储的hdfs的路径的

a1.sinks.k1.hdfs.path = /data/flume/test_02
a1.sinks.k1.hdfs.filePrefix = log
a1.sinks.k1.hdfs.fileSuffix = .1811
a1.sinks.k1.hdfs.rollInterval = 60
a1.sinks.k1.hdfs.rollSize = 134217728 
a1.sinks.k1.hdfs.rollCount = 10000

#指定agent的通道

a1.channels.c1.type = memory

#绑定agent的  r1   c1   k1 

a1.sources.r1.channels = c1
a1.sinks.k1.channel = c1	



```



启动：

```
flume-ng agent --conf conf --conf-file /home/hadoop/apps/apache-flume-1.8.0-bin/conf/test_hdfs_sink02.conf --name a1
```



日志：
log.1554705686806（当前系统的时间戳）.1811.tmp

正常采集日志的时候  
一般文件夹格式 /order/2019-04-08/
文件夹不是手动写死的   根据时间自动生成的
如何自动生成文件夹的名字

eg:

```shell
指定当前agent a1的 sources sinks  channels 的别名

a1.sources = r1
a1.sinks = k1
a1.channels = c1

agent的数据源的

a1.sources.r1.type = exec
a1.sources.r1.command = cat  /home/hadoop/apps/apache-flume-1.8.0-bin/conf/flume-env.sh

指定agent的sink的

a1.sinks.k1.type = hdfs

指定存储的hdfs的路径的

a1.sinks.k1.hdfs.path = /order/%Y-%m-%d/%H-%M-%S
a1.sinks.k1.hdfs.filePrefix = log
a1.sinks.k1.hdfs.fileSuffix = .1811
a1.sinks.k1.hdfs.rollInterval = 60
a1.sinks.k1.hdfs.rollSize = 134217728 
a1.sinks.k1.hdfs.rollCount = 10000

给拦截器起个别名
a1.sources.r1.interceptors = i1
指定拦截器类型
a1.sources.r1.interceptors.i1.type=timestamp

指定agent的通道
a1.channels.c1.type = memory

绑定agent的  r1   c1   k1 
a1.sources.r1.channels = c1
a1.sinks.k1.channel = c1	



```



启动：

```
flume-ng agent --conf conf --conf-file /home/hadoop/apps/apache-flume-1.8.0-bin/conf/test_hdfs_sink03.conf --name a1	
```



报错：
java.lang.NullPointerException: Expected timestamp in the Flume event headers, but it was null
解析目录结构使用系统时间  默认情况event header中解析的
header{null},body{}

解决：
将时间戳加到  头信息中
需要使用flume Interceptors
Interceptors   拦截器    可以拦截数据源  可以添加数据源头信息

拦截器信息：

```
给拦截器起个别名
a1.sources.r1.interceptors = i1

指定拦截器类型
a1.sources.r1.interceptors.i1.type=timestamp
```



headers:{timestamp=1554707017331

进行回滚的时候  以文件为基准的
给文件夹命名的时候 时间粒度 》 文件回滚的粒度



### 3）Avro Sink

​	数据最终收集在一个avro的指定端口中
​	avro source 
​	联合  做多个agent的串联

### 案例：

跨服务器传输.

hdp03上采集数据,传到hdp02上面flume接受,然后写入hdfs

agent1:   hdp03
exec --- channel ----> avro sink 
test_avro_agent01

```
指定当前agent a1的 sources sinks  channels 的别名

a1.sources = r1
a1.sinks = k1
a1.channels = c1

agent的数据源的

a1.sources.r1.type = exec
a1.sources.r1.command = cat  /home/hadoop/apps/apache-flume-1.8.0-bin/conf/flume-env.sh

指定agent的sink的

a1.sinks.k1.type = avro
a1.sinks.k1.hostname = hdp02
a1.sinks.k1.port = 44555

指定agent的通道

a1.channels.c1.type = memory

绑定agent的  r1   c1   k1 

a1.sources.r1.channels = c1
a1.sinks.k1.channel = c1

```



​	

启动：

```
flume-ng agent --conf conf --conf-file /home/hadoop/apps/apache-flume-1.8.0-bin/conf/test_avro_agent01 --name a1
```



agent2 :  hdp02 
avro source--> channel ---> hdfs sink 
test_avro_agent02

```
指定当前agent a1的 sources sinks  channels 的别名

a1.sources = r1
a1.sinks = k1
a1.channels = c1

agent的数据源的

a1.sources.r1.type = avro
a1.sources.r1.bind = hdp02
a1.sources.r1.port = 44555

指定agent的sink的

a1.sinks.k1.type = hdfs
a1.sinks.k1.hdfs.path = /test/flume01

指定agent的通道

a1.channels.c1.type = memory

绑定agent的  r1   c1   k1 

a1.sources.r1.channels = c1
a1.sinks.k1.channel = c1



```



启动：

```
flume-ng agent --conf conf --conf-file /home/hadoop/apps/apache-flume-1.8.0-bin/conf/test_avro_agent02 --name a1
```

多个agent串联
	从后向前启动
	
	