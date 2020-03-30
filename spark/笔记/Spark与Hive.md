Hive On Spark（跟hive没太大关系，就是使用hive标准（HQL、元数据库、UDF、序列化、反序列化机制））

Hive原来的计算模型是MR，有点慢（将中间结果写入HDFS）

Hive On Spark 使用RDD（DataFrame）

## 背景

Hive默认使用MapReduce作为执行引擎，即Hive on mr。实际上，Hive还可以使用Tez和Spark作为其执行引擎，分别为Hive on Tez和Hive on Spark。由于MapReduce中间计算均需要写入磁盘，而Spark是放在内存中，所以总体来讲Spark比MapReduce快很多。因此，Hive on Spark也会比Hive on mr快。为了对比Hive on Spark和Hive on mr的速度，需要在已经安装了Hadoop集群的机器上安装Spark集群（Spark集群是建立在Hadoop集群之上的，也就是需要先装Hadoop集群，再装Spark集群，因为Spark用了Hadoop的HDFS、YARN等），然后把Hive的执行引擎设置为Spark。

Spark运行模式分为三种1、Spark on YARN 2、Standalone Mode 3、Spark on Mesos。
Hive on Spark默认支持Spark on YARN模式，因此我们选择Spark on YARN模式。Spark on YARN就是使用YARN作为Spark的资源管理器。分为Cluster和Client两种模式。



### hive知识回顾

真正要计算的数据是保存在HDFS中，mysql这个元数据库，保存的是hive表的描述信息，描述了有哪些database、table、以及表有多少列，每一列是什么类型，还要描述表的数据保存在hdfs的什么位置？

 

#### hive跟mysql的区别？

hive是一个数据仓库（存储数据并分析数据，分析数据仓库中的数据量很大，一般要分析很长的时间）

mysql是一个关系型数据库（关系型数据的增删改查（低延迟））

 

 

#### hive的元数据库中保存怎知要计算的数据吗？

不保存，保存hive仓库的表、字段、等描述信息

 

#### 真正要计算的数据保存在哪里了？

保存在HDFS中了



### hive的元数据库的功能

建立了一种映射关系，执行HQL时，先到MySQL元数据库中查找描述信息，然后根据描述信息生成任务，然后将任务下发到spark集群中执行



####  spark 和 hive 的版本有一定的要求,版本对应如下
hive-version   spark-version
 master              2.3.0
 3.0.x              2.3.0
 2.3.x              2.0.0
 2.2.x              1.6.0
 2.1.x              1.6.0
 2.0.x              1.5.0
 1.2.x              1.3.1
 1.1.x              1.2.0



### spark读取hive数据



### 开发配置

在idea中开发，整合hive

```xml
<!-- spark如果想整合Hive，必须加入hive的支持 -->

<dependency>

<groupId>org.apache.spark</groupId>

<artifactId>spark-hive_2.11</artifactId>

<version>2.2.0</version>

</dependency>

```



//如果想让hive运行在spark上，一定要开启spark对hive的支持

```scala
val spark = SparkSession.builder()

.appName("HiveOnSpark")

.master("local[*]")

.enableHiveSupport()//启用spark对hive的支持(可以兼容hive的语法了)

.getOrCreate()

```






### 生产环境配置

