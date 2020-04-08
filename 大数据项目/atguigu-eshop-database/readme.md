注明: 项目来源于尚硅谷电商数仓项目

### 操作步骤:
 - 1 打包eshop-mock  
    这里是生成日志事件的代码.
    需要修改 logback.xml中目录的位置.
    然后打包,把jar包上传到hdfs上面 /home/tony/eshop 目录下.
    运行  java -jar  eshop-mock-1.0-SNAPSHOT-jar-with-dependencies.jar
    
 - 2 打包 flume-interceptor  
    这里是flume的拦截器代码
    两个拦截器
    打包后,把不带dependencies的jar上传到  flume/lib下面
    
 - 3 配置flume 的配置文件

    flume/conf  下面创建文件 file-flume-kafka.conf
    读取日志文件,并过滤,传入kafka,不同的类型,被分配到不同的topic中去.
    
    flume/conf   目录下创建  kafka-flume-hdfs.conf文件
    消费kafka的数据,写入hdfs中.根据不同的topic写入到不同的文件中
    
 - 3 kafka
    创建topic
    在 kafka 路径下执行
    ```shell
    $ bin/kafka-topics.sh --zookeeper hadoop1:2181,hadoop2:2181,hadoop3:2181  --create --replication-factor 1 --partitions 1 --topic topic_start
    ```
    
    ```sbtshell
       bin/kafka-topics.sh --zookeeper hadoop1:2181,hadoop2:2181,hadoop3:2181  --create --replication-factor 1 --partitions 1 --topic topic_event

    ```
    
 - 4 启动
    hdfs  :  start-all
    zookeeper  /app/zookeeper/bin/zkServer.sh start
    kafka  : ./kafka-server-start.sh -daemon ../config/server.properties
    flume  : 
    
    ​	hadoop1  上
    
    ```shell
    bin/flume-ng agent --conf conf/ --name a1 --conf-file conf/file-flume-kafka.conf -Dflume.root.logger=INFO,console
    
    ```
    
    ​	hadoop2 上
    
    ```shell
    bin/flume-ng agent --conf conf/ --name a1 --conf-file conf/kafka-flume-hdfs.conf -Dflume.root.logger=INFO,console
    ```
    
    
    
     