案例（Python任务提交）：
spark-submit –master spark://192.168.1.10:7077 –name router_app –total-executor-cores 8 –executor-memory 4g router_inout.py

常用的重要参数详解：
1) –master MASTER_URL: 指定要连接的集群模式（集群资源管理器）
standalone模式: spark://host:port, 如：spark://192.168.1.10:7077
Spark On Mesos模式 : mesos://host:port
Spark On YARN模式: yarn://host:port
本地模式：local

2) – deploy-mode DEPLOY_MODE : 指定任务的提交方式（client 和cluster）
client: 本地客户端模式(默认方式)，一般会在集群主节点安装客户端
cluster: 集群工作节点模式
任务最终都会提交给主节点处理，所以在指定任务提交方式时，考虑本地客户端和集群工作节点对主节点的网络开销问题即可。

3）–name appName :设置任务的名称，方便在webUI查看

4）–py-files PY_FILES ：加载Python外部依赖文件

5）–driver-memory MEM：设置driver的运行内存（占用客户端内存，用于通信及调度开销，默认为1G）

6）–executor-memory MEM：设置每一个executor的运行内存（占用工作节点内存，主要用于执行任务的内存开销），executor代表work节点上的一个进程。

7）–total-executor-cores NUM：设置任务占用的总CPU核数（即任务的并发量），由主节点指定各个工作节点CPU的使用数。
注意：该参数选项只在Spark standalone and Mesos 模式下有效

8）–executor-cores NUM：设置执行任务的每一个executor的CPU核数（yarn模式有效，默认为1）或者工作节点的总CPU核数（standalone模式有效）

9）–num-executors NUM：设置任务的executor进程数（yarn模式下有效）

10）–conf PROP=VALUE：设置Spark的属性参数
–conf spark.default.parallelism=1000 设置RDD分区大小,系统默认为200
–conf spark.storage.memoryFraction=0.5 设置内存分配大小（存储），系统默认为0.6
–conf spark.shuffle.memoryFraction=0.3 设置shuffle上限内存空间，系统默认为0.2



参数调优

```shell
1.num-executors  线程数：一般设置在50-100之间，必须设置，不然默认启动的executor非常少，不能充分利用集群资源，运行速度慢
2.executor-memory 线程内存：参考值4g-8g,num-executor乘以executor-memory不能超过队列最大内存，申请的资源最好不要超过最大内存的1/3-1/2
3.executor-cores 线程CPU core数量：core越多，task线程就能快速的分配，参考值2-4，num-executor*executor-cores的1/3-1/2
 
1.spark-submit spark提交
2.--queue spark 在spark队列
3.--master yarn 在yarn节点提交
4.--deploy-mode client 选择client模型，还是cluster模式；在同一个节点用client,在不同的节点用cluster
5.--executor-memory=4G 线程内存：参考值4g-8g,num-executor乘以executor-memory不能超过队列最大内存，申请的资源最好不要超过最大内存的1/3-1/2
6.--conf spark.dynamicAllocation.enabled=true 是否启动动态资源分配
7.--executor-cores 2 线程CPU core数量：core越多，task线程就能快速的分配，参考值2-4，num-executor*executor-cores的1/3-1/2
8.--conf spark.dynamicAllocation.minExecutors=4 执行器最少数量
9.--conf spark.dynamicAllocation.maxExecutors=10 执行器最大数量
10.--conf spark.dynamicAllocation.initialExecutors=4 若动态分配为true,执行器的初始数量
11.--conf spark.executor.memoryOverhead=2g 堆外内存：处理大数据的时候，这里都会出现问题，导致spark作业反复崩溃，无法运行；此时就去调节这个参数，到至少1G（1024M），甚至说2G、4G）
12.--conf spark.speculation=true 推测执行：在接入kafaka的时候不能使用，需要考虑情景
13.--conf spark.shuffle.service.enabled=true 提升shuffle计算性能
```



**动态改变参数：**

```shell
spark-submit 
--queue spark 
--master yarn 
--deploy-mode client
--executor-memory=4G
--conf spark.dynamicAllocation.enabled=true
--executor-cores 2
--conf spark.dynamicAllocation.minExecutors=4
--conf spark.dynamicAllocation.maxExecutors=10
--conf spark.dynamicAllocation.initialExecutors=4
--conf spark.executor.memoryOverhead=2g
--conf spark.speculation=true
--conf spark.shuffle.service.enabled=true
--class com.practice  Spark.jar
```

**静态参数：**

```shell
spark-submit
--master yarn
--deploy-mode client
--executor-memory 10G
--num-executors 20
--executor-cores 2
--driver-memory 8g
--conf spark.driver.maxResultSize=6G
--conf spark.network.timeout=300
--conf spark.executor.heartbeatInterval=30
--conf spark.task.maxFailures=4 --queue spark
--conf spark.speculation=true
--conf spark.shuffle.service.enabled=true
--conf spark.executor.memoryOverhead=8g
--class com.practice Spark.jar
 
 
spark-submit
--master yarn
--deploy-mode client
--executor-memory 14G
--num-executors 20
--executor-cores 4
--driver-memory 8g
--conf spark.driver.maxResultSize=8G
--conf spark.network.timeout=300
--conf spark.executor.heartbeatInterval=30
--conf spark.task.maxFailures=4
--queue spark --conf spark.speculation=true
--conf spark.shuffle.service.enabled=true
--conf spark.executor.memoryOverhead=10g
--class com.practice Spark.jar 
```

```
cluster 提交可以关闭命令窗口，后台运行。运于生产环境

client 提交不可以关闭命令窗口。用于调试


————————————————
版权声明：本文为CSDN博主「从一点一滴做起」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
原文链接：https://blog.csdn.net/qq_39313597/article/details/89947187
```



原文:https://blog.csdn.net/qq_39313597/article/details/89947187