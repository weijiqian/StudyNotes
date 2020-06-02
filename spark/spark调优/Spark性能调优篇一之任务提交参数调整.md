### 问题一：有哪些资源可以分配给spark作业使用？

答案：executor个数，cpu per exector（每个executor可使用的CPU个数），memory per exector（每个executor可使用的内存），driver memory







### 问题二：在什么地方分配资源给spark作业？

答案：很简单，就是在我们提交spark作业的时候的脚本中设定，具体如下（这里以我的项目为例）

```
/usr/local/spark/bin/spark-submit \

--class  com.xingyun.test.WordCountCluster \

--num-executors    3             \*配置executor的数量 *\

--driver-memory    100m       \*配置driver的内存（影响不大）*\

--executor-memory   100m   \*配置每个executor的内存大小 *\

--executor-cores   3               \*配置每个executor的cpu core数量 *\

/usr/local/SparkTest-0.0.1-SNAPSHOT-jar-with-dependencies.jar  \

```



我们该如何设定这些参数的大小呢？下面分两种情况讨论。

case1：把spark作业提交到Spark Standalone上面。一般自己知道自己的spark测试集群的机器情况。举个例子：比如我们的测试集群的机器为每台4G内存，2个CPU core，5台机器。这里以可以申请到最大的资源为例，那么  --num-executors  参数就设定为 5，那么每个executor平均分配到的资源为：--executor-memory 参数设定为4G，--executor-cores 参数设定为 2 。

case2：把spark作业提交到Yarn集群上去。那就得去看看要提交的资源队列中大概还有多少资源可以背调度。举个例子：假如可调度的资源配置为：500G内存，100个CPU core，50台机器。 --num-executors  参数就设定为 50，那么每个executor平均分配到的资源为：--executor-memory 参数设定为 10G，--executor-cores 参数设定为 2



### 问题三：为什么分配了这些资源以后，我们的spark作业的性能就会得到提升呢？

因为是调优后呀.



作者：z小赵
链接：https://www.jianshu.com/p/d07e79c22d90
来源：简书
著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。