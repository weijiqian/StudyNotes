

[TOC]

flume的高级应用
=============================================

为了防止数据采集的时候的数据的丢失
	将进行汇总的agent  设置一个备份的agent  防止汇总的agent的单点故障问题
	数据收集的时候  有一个优先级问题
	主agent的优先级 高 
	从agent的优先级  低
	将多个主备的agent放在一个组中  最终这个组中只有一个agent收集数据  其余的agent处于观望状态的
	
配置高可用的实现：  汇总数据  hdfs上
hdp01   webserver
hdp02  webserver
hdp03    webserver 


hdp03   汇总的主 agent 
hdp04   hdp03 备份的 

webserver 端的agent:  hdp01   hdp02   hdp03 
source exec   cat 
channel memory
sink   avro  发送进行汇总数据的  主agent | 备份的 agent

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



配置两个汇总的agent   hdp03   hdp04 

hdp03   主agent  10 

hdp04  备份

source avro 
channel memory
sink  hdfs 



hdp03的配置

```
#设置别名
a2.sources = r1
a2.channels = c1
a2.sinks = k1

设置source 

当前主机为什么，就修改成什么主机名

a2.sources.r1.type = avro
a2.sources.r1.bind = hdp03
a2.sources.r1.port = 52020
a2.sources.r1.interceptors = i1
a2.sources.r1.interceptors.i1.type = static
a2.sources.r1.interceptors.i1.key = Collector

当前主机为什么，就修改成什么主机名

a2.sources.r1.interceptors.i1.value = hdp03

#指定channnel
a2.channels.c1.type = memory
a2.channels.c1.capacity = 1000
a2.channels.c1.transactionCapacity = 100

#指定sink
a2.sinks.k1.type=hdfs
a2.sinks.k1.hdfs.path= /flume_ha/loghdfs
a2.sinks.k1.hdfs.fileType=DataStream
a2.sinks.k1.hdfs.writeFormat=TEXT
a2.sinks.k1.hdfs.rollInterval=10
a2.sinks.k1.hdfs.filePrefix=%Y-%m-%d

指定绑定

a2.sources.r1.channels = c1
a2.sinks.k1.channel=c1



```

hdp04   备agent   1

```
#设置别名
a2.sources = r1
a2.channels = c1
a2.sinks = k1

设置source 

当前主机为什么，就修改成什么主机名

a2.sources.r1.type = avro
a2.sources.r1.bind = hdp04
a2.sources.r1.port = 52020
a2.sources.r1.interceptors = i1
a2.sources.r1.interceptors.i1.type = static
a2.sources.r1.interceptors.i1.key = Collector

当前主机为什么，就修改成什么主机名

a2.sources.r1.interceptors.i1.value = hdp04

#指定channnel
a2.channels.c1.type = memory
a2.channels.c1.capacity = 1000
a2.channels.c1.transactionCapacity = 100

#指定sink
a2.sinks.k1.type=hdfs
a2.sinks.k1.hdfs.path= /flume_ha/loghdfs
a2.sinks.k1.hdfs.fileType=DataStream
a2.sinks.k1.hdfs.writeFormat=TEXT
a2.sinks.k1.hdfs.rollInterval=10
a2.sinks.k1.hdfs.filePrefix=%Y-%m-%d

指定绑定

a2.sources.r1.channels = c1
a2.sinks.k1.channel=c1


```



启动：
先启动 hdp03 agent_ha_hz01

```shell
bin/flume-ng agent -c conf -f /home/hadoop/apps/apache-flume-1.8.0-bin/conf/agent_ha_hz01 -n a2 -Dflume.root.logger=INFO,console

```



在启动  hdp04  备份的agent 

```shell
bin/flume-ng agent -c conf -f /home/hadoop/apps/apache-flume-1.8.0-bin/conf/agent_ha_hz02 -n a2 -Dflume.root.logger=INFO,console

```



启动web server 

```shell
bin/flume-ng agent -c conf -f /home/hadoop/apps/apache-flume-1.8.0-bin/conf/agent_ha01 -n agent1 -Dflume.root.logger=INFO,console

bin/flume-ng agent -c conf -f /home/hadoop/apps/apache-flume-1.8.0-bin/conf/agent_ha02 -n agent1 -Dflume.root.logger=INFO,console

bin/flume-ng agent -c conf -f /home/hadoop/apps/apache-flume-1.8.0-bin/conf/agent_ha03 -n agent1 -Dflume.root.logger=INFO,console

```


