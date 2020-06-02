### 在kafka中ISR是什么？

**ISR**（in sync replica）：是kafka动态维护的一组同步副本，在ISR中有成员存活时，只有这个组的成员才可以成为leader，内部保存的为每次提交信息时必须同步的副本（acks = all时），每当leader挂掉时，在ISR集合中选举出一个follower作为leader提供服务，当ISR中的副本被认为坏掉的时候，会被踢出ISR，当重新跟上leader的消息数据时，重新进入ISR。

**OSR**（out sync replica）: 保存的副本不必保证必须同步完成才进行确认，OSR内的副本是否同步了leader的数据，不影响数据的提交，OSR内的follower尽力的去同步leader，可能数据版本会落后。

### kafka如何控制需要同步多少副本才可以返回确定到生产者消息才可用？

- 当写入到kakfa时，生产者可以选择是否等待0（只需写入leader）,1（只需同步一个副本） 或 -1（全部副本）的消息确认（这里的副本指的是ISR中的副本）。

### 对于kafka节点活着的条件是什么？

- 第一点：一个节点必须维持和zk的会话，通过zk的心跳检测实现
- 第二点：如果节点是一个slave也就是复制节点，那么他必须复制leader节点不能太落后。这里的落后可以指两种情况
  - 1：数据复制落后，slave节点和leader节点的数据相差较大，这种情况有一个缺点，在生产者突然发送大量消息导致网络堵塞后，大量的slav复制受阻，导致数据复制落后被大量的踢出ISR。
  - 2：时间相差过大，指的是slave向leader请求复制的时间距离上次请求相隔时间过大。通过配置`replica.lag.time.max`就可以配置这个时间参数。这种方式解决了上述第一种方式导致的问题。



### kafka分区partition挂掉之后如何恢复？

在kafka中有一个partition recovery机制用于恢复挂掉的partition。

每个Partition会在磁盘记录一个RecoveryPoint（恢复点）, 记录已经flush到磁盘的最大offset。当broker fail 重启时,会进行loadLogs。

### 什么原因导致副本与leader不同步的呢？

- 慢副本：在一定周期时间内follower不能追赶上leader。最常见的原因之一是I / O瓶颈导致follower追加复制消息速度慢于从leader拉取速度。
- 卡住副本：在一定周期时间内follower停止从leader拉取请求。follower replica卡住了是由于GC暂停或follower失效或死亡。
- 新启动副本：当用户给主题增加副本因子时，新的follower不在同步副本列表中，直到他们完全赶上了leader日志。







作者：匠心Java
链接：https://juejin.im/post/5c46e729e51d452c8e6d5679
来源：掘金
著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。