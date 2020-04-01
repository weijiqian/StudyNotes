### 1）Kafka压测

用Kafka官方自带的脚本，对Kafka进行压测。Kafka压测时，可以查看到哪个地方出现了瓶颈（CPU，内存，网络IO）。一般都是网络IO达到瓶颈。 

kafka-consumer-perf-test.sh

kafka-producer-perf-test.sh

###  2）Kafka Producer压力测试

（1）在/opt/module/kafka/bin目录下面有这两个文件。我们来测试一下

```
[atguigu@hadoop102 kafka]$ bin/kafka-producer-perf-test.sh  --topic test --record-size 100 --num-records 100000 --throughput 1000 --producer-props bootstrap.servers=hadoop102:9092,hadoop103:9092,hadoop104:9092

```



说明：record-size是一条信息有多大，单位是字节。num-records是总共发送多少条信息。throughput 是每秒多少条信息。

（2）Kafka会打印下面的信息

5000 records sent, 999.4 records/sec (0.10 MB/sec), 1.9 ms avg latency, 254.0 max latency.

5002 records sent, 1000.4 records/sec (0.10 MB/sec), 0.7 ms avg latency, 12.0 max latency.

5001 records sent, 1000.0 records/sec (0.10 MB/sec), 0.8 ms avg latency, 4.0 max latency.

5000 records sent, 1000.0 records/sec (0.10 MB/sec), 0.7 ms avg latency, 3.0 max latency.

5000 records sent, 1000.0 records/sec (0.10 MB/sec), 0.8 ms avg latency, 5.0 max latency.

参数解析：本例中一共写入10w条消息，每秒向Kafka写入了***\*0.10\*******\*MB\****的数据，平均是1000条消息/秒，每次写入的平均延迟为0.8毫秒，最大的延迟为254毫秒。

#### 3）Kafka Consumer压力测试

Consumer的测试，如果这四个指标（IO，CPU，内存，网络）都不能改变，考虑增加分区数来提升性能。

[atguigu@hadoop102 kafka]$ 

bin/kafka-consumer-perf-test.sh --zookeeper hadoop102:2181 --topic test --fetch-size 10000 --messages 10000000 --threads 1

参数说明：

--zookeeper 指定zookeeper的链接信息

--topic 指定topic的名称

--fetch-size 指定每次fetch的数据的大小

--messages 总共要消费的消息个数

测试结果说明：

start.time, ***\*end.time,\**** data.consumed.in.MB, ***\*MB.sec,\**** data.consumed.in.nMsg***\*, nMsg.sec\****

2019-02-19 20:29:07:566, ***\*2019-02-19 20:29:12:170,\**** 9.5368, ***\*2.0714,\**** 100010, ***\*21722.4153\****

***\*开始测试\*******\*时间，测试结束数据，最大吞吐\*******\*率\****9.5368MB/s，平均每秒消费***\*2.0714MB/s\*******\*，\*******\*最大每秒消费\****100010条，平均每秒消费***\*21722.4153\*******\*条。\****

