#### kafka 命令

1. 启动，在kafka的bin目录下

   ```
    ./kafka-server-start.sh -daemon ../config/server.properties
   ```

   输入 jps， 查看是否启动成功。

2. 查看topic列表：

   ```
    bin/kafka-topics.sh --zookeeper localhost:2181 --list
   ```

3. 新建topic

   ```
    ./bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic test_kafka
   
   ```

4. 查看某一topic的详细信息

   ```
    bin/kafka-topics.sh --zookeeper localhost:2181 --describe --topic test
   
   ```

5. 删除topic

   ```
    bin/kafka-topics.sh --zookeeper localhost:2181 --delete --topic test
   
   ```

6. 启动producer

   ```
    bin/kafka-console-producer.sh --broker-list localhost:9092 --topic test
   
   ```

7. 启动consumer

   ```
    bin/kafka-console-consumer.sh --bootstrap-server ip:9092 --topic test --from-beginning
    集群就写多个ip+端口，用逗号分开。
    ip1:9092,ip2:9092,ip3:9092 
   
   ```

8. 查看offset

   ```
    bin/kafka-run-class.sh kafka.tools.ConsumerOffsetChecker --zookeeper :2181,ip2:2181,ip3:2181 --group test_A --topic test
   
   ```

9. 设置offset

   ```
    bin/kafka-consumer-groups.sh --bootstrap-server ip1:9092,ip2:9092,ip3:9092 --group test_A
   ```


作者：Dictator丶
链接：https://juejin.im/post/5c0a2fa1e51d451dad5d93bf
来源：掘金