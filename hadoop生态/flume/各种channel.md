**Memory Channel**   event保存在Java Heap中。如果允许数据小量丢失，推荐使用

**File Channel**          event保存在本地文件中，可靠性高，但吞吐量低于Memory Channel

**JDBC Channel**     event保存在关系数据中，一般不推荐使用

**KafkaChannel**



### 1.Memory Channel

基于内存的channel，实际就是将event存放于内存中一个固定大小的队列中。其优点是速度快，缺点是可能丢失数据。
可配置属性如下：

| 属性                         | 默认 | 说明                                                |
| :--------------------------- | :--- | :-------------------------------------------------- |
| **type**                     | –    | 这里为memory                                        |
| capacity                     | 100  | 存储在channel中的最大event个数                      |
| transactionCapacity          | 100  | 每个事务中从source获取或者发送到sink的event最大个数 |
| keep-alive                   | 3    | 添加或者删除一个event的超时时间，单位为秒           |
| byteCapacityBufferPercentage | 20   | byteCapacity和预估的所有event大小之间的buffer。     |
| byteCapacity                 |      | 内存里面允许存放的所有event的字节的最大值。         |
|                              |      |                                                     |

配置示例：

```
a1.channels = c1
a1.channels.c1.type = memory
a1.channels.c1.capacity = 10000
a1.channels.c1.transactionCapacity = 10000
a1.channels.c1.byteCapacityBufferPercentage = 20
a1.channels.c1.byteCapacity = 800000
2.JDBC Channel
```



### 2.JDBC Channel

将event存放于一个支持JDBC连接的数据库中，目前官方推荐的是Derby库，其优点是数据可以恢复。
可配置属性如下：

| 属性                       | 默认                                 | 说明                                                         |
| :------------------------- | :----------------------------------- | :----------------------------------------------------------- |
| **type**                   | –                                    | 这里为jdbc                                                   |
| db.type                    | DERBY                                | 数据库类型                                                   |
| driver.class               | org.apache.derby.jdbc.EmbeddedDriver | 数据库驱动类                                                 |
| driver.url                 |                                      | jdbc connnection url                                         |
| db.username                | sa                                   | 用户名                                                       |
| db.password                | –                                    | 密码                                                         |
| connection.properties.file | –                                    | 连接配置文件                                                 |
| create.scheme              | true                                 | 如果scheme不存在是否创建                                     |
| create.index               | true                                 | 是否创建索引                                                 |
| create.foreignkey          | true                                 | 是否可以有外键约束                                           |
| transaction.isolation      | READ_COMMITTED                       | 事务隔离机制。可选项有： READ_UNCOMMITTED READ_COMMITTED SERIALIZABLE REPEATABLE_READ |
| maximum.connections        | 10                                   | 连接数                                                       |
| maximum.capacity           | 0（无限制）                          | channel中最大的event个数                                     |
| sysprop.*                  |                                      | db的特殊配置                                                 |
| sysprop.user.home          |                                      | Derby库的存放路径                                            |

配置示例：

```
a1.channels = c1
a1.channels.c1.type = jdbc
```



### 3.File Channel

在磁盘上指定一个目录用于存放event，同时也可以指定目录的大小。优点是数据可恢复，相对于memory channel来说缺点是要频繁的读取磁盘，速度较慢。
可配置属性如下：

| 属性                                        | 默认                                   | 说明                                                         |
| :------------------------------------------ | :------------------------------------- | :----------------------------------------------------------- |
| **type**                                    | –                                      | 这里为file                                                   |
| checkpointDir                               | ~/.flume(水道)/file-channel/checkpoint | 检查点存放目录                                               |
| useDualCheckpoints                          | false                                  | 检查点的备份。如果这个参数设置为true，backupCheckpointDir必须设置。 |
| backupCheckpointDir                         | –                                      | 此目录作为检查点目录的备用目录， 必须与checkpointDir不同     |
| dataDirs                                    | ~/.flume/file-channel/data             | 可以使用逗号分隔多个路径， 使用在不同磁盘上的多个路径能提升channel的表现 |
| transactionCapacity                         | 10000                                  | channel中能支持的事务的最大数量。                            |
| maxFileSize                                 | 2146435071                             | 单个文件的最大字节数。                                       |
| minimumRequiredSpace                        | 524288000                              | 需要的最小空闲空间，单位为byte。                             |
| capacity                                    | 1000000                                | channel 的最大容量                                           |
| keep-alive                                  | 3                                      | 等待put操作的总时间，单位为秒。                              |
| use-log-replay–v1                           | false                                  | 使用旧的replay逻辑。                                         |
| use-fast-replay                             | false                                  | replay时不使用队列                                           |
| checkpointOnClose                           | true                                   | 控制是否创建一个checkpoint 当channel关闭的时候。             |
| encryption.activeKey                        | –                                      | 用来加密数据的key的名称。                                    |
| encryption.cipherProvider                   | –                                      | 加密方式，支持的类型有AESCTRNOPADDING                        |
| encryption.keyProvider                      | –                                      | key的类型，支持的类型有JCEKSFILE                             |
| encryption.keyProvider.keyStoreFile         |                                        | key文件存放的路径                                            |
| encrpytion.keyProvider.keyStorePasswordFile |                                        | 密码存放的路径                                               |
| encryption.keyProvider.keys                 | –                                      | 多个key                                                      |
| encyption.keyProvider.keys.*.passwordFile   | –                                      | 多个密码文件                                                 |

配置示例如下：

```
a1.channels = c1
a1.channels.c1.type = file
a1.channels.c1.checkpointDir = /mnt/flume/checkpoint
a1.channels.c1.dataDirs = /mnt/flume/data

```



### 4.Spillable Memory Channel

event存放在内存和磁盘上，内存作为主要存储，当内存达到一定临界点的时候会溢写到磁盘上。其中和了memory channel和File channel的优缺点。
可配置属性如下：

| 属性                         | 默认      | 说明                                                        |
| :--------------------------- | :-------- | :---------------------------------------------------------- |
| **type**                     | –         | 这里为SPILLABLEMEMORY                                       |
| memoryCapacity               | 10000     | 内存队列中可以存放的最大event个数                           |
| overflowCapacity             | 100000000 | 溢写空间能存放的event的最大值。 如果不想使用溢写，此值设为0 |
| overflowTimeout              | 3         | 当内存写满开始溢写到磁盘上的等待时间，单位为秒。            |
| byteCapacityBufferPercentage | 20        | byteCapacity和预估的所有event大小之间的buffer。             |
| byteCapacity                 |           | 内存里面允许存放的所有event的字节的最大值。                 |
| avgEventSize                 | 500       | 预估event的平均字节。                                       |
|                              |           |                                                             |
|                              |           |                                                             |

配置示例如下：

```
a1.channels = c1
a1.channels.c1.type = SPILLABLEMEMORY
a1.channels.c1.memoryCapacity = 10000
a1.channels.c1.overflowCapacity = 1000000
a1.channels.c1.byteCapacity = 800000
a1.channels.c1.checkpointDir = /mnt/flume/checkpoint
a1.channels.c1.dataDirs = /mnt/flume/data

```

————————————————
版权声明：本文为CSDN博主「围城客」的原创文章，遵循 CC 4.0 BY-SA 版权协议，转载请附上原文出处链接及本声明。
原文链接：https://blog.csdn.net/u014612521/article/details/103058809