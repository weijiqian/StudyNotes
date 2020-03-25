# kafka中的配额管理

对Producer和Consumer的produce&fetch操作进行流量限制



可配置的对象

- user + clientid
- user
- clientid



clientid是每个接入kafka集群的client的一个身份标志，在ProduceRequest和FetchRequest中都需要带上；

user只有在开启了身份认证的kafka集群才有



可配置的选项包括：

- `producer_byte_rate`。发布者单位时间（每秒）内可以发布到**单台broker**的字节数。
- `consumer_byte_rate`。消费者单位时间（每秒）内可以从**单台broker**拉取的字节数。



## 2 如何配置

可以通过两种方式来作配额管理：

- 在配置文件中指定所有client-id的统一配额。

- 动态修改zookeeper中相关znode的值，可以配置指定client-id的配额。

  使用第一种方式，必须重启broker，而且还不能针对特定client-id设置。所以，推荐大家使用第二种方式。

#### 2.1 使用官方脚本修改配额

kafka官方的二进制包中，包含了一个脚本bin/kafka-configs.sh，支持针对user，client-id，(user,client-id)等三种纬度设置配额（也是通过修改zk来实现的）。

配置user+clientid。例如，user为”user1”，clientid为”clientA”。

```shell
bin/kafka-configs.sh  
--zookeeper localhost:2181 
--alter --add-config 'producer_byte_rate=1024,consumer_byte_rate=2048' 
--entity-type users 
--entity-name user1 
--entity-type clients 
--entity-name clientA

```


配置user。例如，user为”user1”

```shell
bin/kafka-configs.sh  
--zookeeper localhost:2181 
--alter --add-config 'producer_byte_rate=1024,consumer_byte_rate=2048' 
--entity-type users 
--entity-name user1
```


配置client-id。例如，client-id为”clientA”

```shell
bin/kafka-configs.sh 
--zookeeper localhost:2181 
--alter --add-config 'producer_byte_rate=1024,consumer_byte_rate=2048' 
--entity-type clients 
--entity-name clientA

```


#### 2.2 直接写zk来修改配额

如果我们希望能够在代码里面直接写zk来实现配额管理的话，那要怎样操作呢？

假定我们在启动kafka时指定的zookeeper目录是kafka_rootdir。

配置user+clientid。例如，针对”user1”，”clientA”的配额是10MB/sec，其它clientid的默认配额是5MB/sec。 

```
znode: ${kafka_rootdir}/config/users/user1/clients/clientid; value: {"version":1,"config":{"producer_byte_rate":"10485760","consumer_byte_rate":"10485760"}}
znode: {kafka_rootdir}/config/users/user1/clients/<default>; value: {"version":1,"config":{"producer_byte_rate":"5242880","consumer_byte_rate":"5242880"}}

```



配置user。例如，”user2”的配额是1MB/sec，其它user的默认配额是5MB/sec。

```
znode: ${kafka_rootdir}/config/users/user1; value: {"version":1,"config":{"producer_byte_rate":"1048576","consumer_byte_rate":"1048576"}}
znode: ${kafka_rootdir/config/users/<default>; value: {"version":1,"config":{"producer_byte_rate":"5242880","consumer_byte_rate":"5242880"}}
```



配置client-id。例如，”clientB”的配额是2MB/sec，其它clientid的默认配额是1MB/sec。 

```
znode:${kafka_rootdir}/config/clients/clientB'; value:{“version”:1,”config”:{“producer_byte_rate”:”2097152”,”consumer_byte_rate”:”2097152”}}</li> 
<li>znode:${kafka_rootdir}/config/clients/; value:{“version”:1,”config”:{“producer_byte_rate”:”1048576”,”consumer_byte_rate”:”1048576”}}`
```


无论是使用官方的脚本工具，还是自己写zookeeper，最终都是将配置写入到zk的相应znode。所有的broker都会watch这些znode，在数据发生变更时，重新获取配额值并及时生效。为了降低配额管理的复杂度和准确度，kafka中每个broker各自管理配额。所以，上面我们配置的那些额度值都是单台broker所允许的额度值。



[引用原文](https://blog.csdn.net/shuxiaogd/article/details/79390938)

