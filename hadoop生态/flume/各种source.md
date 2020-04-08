## source:

### 1）Exec source   数据源来自于一个linux命令

​	linux命令的执行结果
​		cat ....
​		tail -100 ... $$
​		tail -f ... 
​			
案例：

```
指定当前agent a1的 sources sinks  channels 的别名

a1.sources = r1
a1.sinks = k1
a1.channels = c1

agent的数据源的

a1.sources.r1.type = exec
a1.sources.r1.command= cat /home/hadoop/zookeeper.out

指定agent的sink的

a1.sinks.k1.type = logger

指定agent的通道

a1.channels.c1.type = memory

绑定agent的  r1   c1   k1 

a1.sources.r1.channels = c1
a1.sinks.k1.channel = c1



```

启动：

```
flume-ng agent --conf conf --conf-file /home/hadoop/apps/apache-flume-1.8.0-bin/conf/test_exec_source.conf --name a1 -Dflume.root.logger=INFO,console
```



### 2）avro source : 监控一个avro协议的端口的

```
指定当前agent a1的 sources sinks  channels 的别名

a1.sources = r1
a1.sinks = k1
a1.channels = c1

agent的数据源的

a1.sources.r1.type = avro
a1.sources.r1.bind = localhost
a1.sources.r1.port = 44555

指定agent的sink的

a1.sinks.k1.type = logger

指定agent的通道

a1.channels.c1.type = memory

绑定agent的  r1   c1   k1 
$\color{#FF3030}{red}$

a1.sources.r1.channels = c1
a1.sinks.k1.channel = c1

```

启动：

```
flume-ng agent --conf conf --conf-file /home/hadoop/apps/apache-flume-1.8.0-bin/conf/test_avro_source.conf --name a1 -Dflume.root.logger=INFO,console

```



发送avro协议的数据：

```
flume-ng avro-client -H localhost -p 44555 --filename /home/hadoop/apps/apache-flume-1.8.0-bin/conf/mytest.conf
```



### 3)thift source 

​		thift 服务的端口

### 4）netcat 

### 5）Spooling Directory Source 文件夹

​	数据来源本地磁盘上的一个指定的文件夹中的数据
​	监听的是给定的文件夹中的 数据   新增的数据

```
指定当前agent a1的 sources sinks  channels 的别名

a1.sources = r1
a1.sinks = k1
a1.channels = c1

agent的数据源的

a1.sources.r1.type = spooldir
a1.sources.r1.spoolDir = /home/hadoop/tmpdata

指定agent的sink的

a1.sinks.k1.type = logger

指定agent的通道

a1.channels.c1.type = memory

绑定agent的  r1   c1   k1 

a1.sources.r1.channels = c1
a1.sinks.k1.channel = c1	



```

启动：

```
flume-ng agent --conf conf --conf-file /home/hadoop/apps/apache-flume-1.8.0-bin/conf/test_spooldir_source.conf --name a1 -Dflume.root.logger=INFO,console
```



spoolDir 监听指定的目录下所有文件
	指定目录下的文件采集之前 不会被修改名字的  数据一旦被采集过文件名后面就会添加一个后缀 .COMPLETED
	指定目录下一旦有新的文件产生  就会被监控到  采集到数据

fileSuffix	.COMPLETED	
prefix

