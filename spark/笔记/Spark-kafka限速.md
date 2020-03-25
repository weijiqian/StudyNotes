## spark端限速

- 在Direct模式下设置**spark.streaming.kafka.maxRatePerPartition**
- receiver模式下设置**spark.streaming.receiver.maxRate**





## kafka端限速

consumer_byte_rate  限速单位都是每秒字节数



通过kafka-configs命令。比如下面命令是为client.id为clientA的consumer设置限速：

$ bin/kafka-configs.sh --zookeeper localhost:2181

--alter

**--add-config'consumer_byte_rate=15728640'**

--entity-type clients

--entity-name clientA

此命令只为client.id=clientA的consumer设置了限速，故在Spark端你还需要显式设置client.id，比如：

Map<String,Object>kafkaParams=newHashMap<>();

...

**kafkaParams.put("client.id","clientA");**

...

JavaInputDStream<ConsumerRecord<String,String>>

stream=KafkaUtils.createDirectStream(...);

值得注意的是，在Kafka端设置的限速单位都是每秒字节数。如果你想按照每秒多少条消息进行限速还需要结合消息的平均大小来计算。