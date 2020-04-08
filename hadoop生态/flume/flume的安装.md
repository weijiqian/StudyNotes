flume的经典部署方案
============================================

1）单 Agent 采集数据
2）多agent串联
3）多 Agent 合并串联
4）多路复用
5）高可用的配置

注意： 每一个agent只能采集当前节点数据


flume的安装
========================================

安装要求：
	1）Java 1.8 or later
	2）Memory 
	3）Disk Space
	4）Directory Permissions - Read/Write permissions for directories used by agent
安装节点：
	在哪里用  就在哪个节点安装
	先安装一个节点
安装：
	1）上传
	2）解压
		tar -xvzf apache-flume-1.8.0-bin.tar.gz
	3）配置环境变量  （可以不配置）
		export FLUME_HOME=/home/hadoop/apps/apache-flume-1.8.0-bin
		export PATH=$PATH:$FLUME_HOME/bin
		

		source /etc/profile
		验证：
		flume-ng version
	4）修改flume的配置文件
		mv flume-env.sh.template flume-env.sh
		export JAVA_HOME=/home/hadoop/apps/jdk1.8.0_73

测试一下flume:
===========================================

配置数据来源source 通道channel 数据目的地sink
通过配置文件指定  手动编辑配置文件  
注意：配置文件名  随意 .conf .txt .png



```
#指定当前agent a1的 sources sinks  channels 的别名

a1.sources = r1
a1.sinks = k1
a1.channels = c1

agent的数据源的

a1.sources.r1.type = netcat
a1.sources.r1.bind = localhost
a1.sources.r1.port = 44444

指定agent的sink的

a1.sinks.k1.type = logger

指定agent的通道

a1.channels.c1.type = memory
a1.channels.c1.capacity = 1000
a1.channels.c1.transactionCapacity = 100

绑定agent的  r1   c1   k1 
a1.sources.r1.channels = c1
a1.sinks.k1.channel = c1

```

这个配置文件中  需要配置核心内容5个
1）agent source channel  sink 的别名 
2）指定agent的  sources
3）指定agent  channel 
4) 指定agent的  sink 
5） 绑定agent的 source channel sink


说明：
a1: 指的就是agent的名字  agent别名

启动agent：

```
命令:
flume-ng agent --conf conf --conf-file /home/hadoop/apps/apache-flume-1.8.0-bin/conf/mytest.conf --name a1 -Dflume.root.logger=INFO,console

```



agent  启动agent 
--conf   指定配置文件方式运行
--conf-file  指定agent的配置文件的位置的
--name   指定当前的agent的别名   一定要和配置文件中一致
-Dflume.root.logger   指定日志存储位置  INFO,console



# 