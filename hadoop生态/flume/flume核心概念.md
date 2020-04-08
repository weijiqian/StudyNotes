agent：代理
	代表的是一个flume数据采集的服务端 flume的代理
	一个agent对应的就是一个jvm进程
	一个agent中包至少含一套  source channel sink的
	一个节点上可以启动多个agent的  多个jvm进程
source:
	数据来源
	数据来源多种形式的  代表flume进行收集数据的来源
	avro source:
		来自于用户指定的节点端口的   ****
	exec source:
		来自于一个linux 命令
	Spooling Directory Source
		来源于一个本地磁盘的一个文件夹的数据变化的
		指定的文件夹中  只要有数据变化  新增
	tcp udp http 
		
channel：
	指定通道
	接受source的数据  做数据的中转站 给sink消费的
	本地磁盘的内存|本地磁盘硬盘
	memory channel :  *****
		内存  作为中转站   
	jdbc channel :
		数据库作为中转
	file channel:
		将source的数据存储在本地磁盘文件上
sink：
	为收集的数据指定，目的地的
	hdfs|  hive | hbase上
	hdfs sink:  ***
		将收集的数据放在hdfs上
	hive sink:很少用
		将数据放在hive中
	avro sink:  ***
		数据存储在用户指定的端口的
	logger sink:  测试
		将收集的数据   打印到控制台显示	
event：
	数据采集的  数据被封装的最小单元
	一条数据  封装成一个event
	数据传输中的最小单元
	event的组成：
		header  头信息   时间戳|来源
		body ：{}  真实数据  key-v