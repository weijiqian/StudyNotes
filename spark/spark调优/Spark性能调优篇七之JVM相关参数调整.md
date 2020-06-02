### **降低cache操作的内存占比**

方案:

通过SparkConf.set("spark.storage.memoryFraction","0.6")来设定。默认是0.6,可以设置为0.5  0.3 等

原因:

spark中，堆内存又被划分成了两块儿，一块儿是专门用来给RDD的cache、persist操作进行RDD数据缓存用的；另外一块儿，就是我们刚才所说的，用来给spark算子函数的运行使用的，存放函数中自己创建的对象。默认情况下，给RDD cache操作的内存占比是0.6，即60%的内存都给了cache操作了。但是问题是，如果某些情况下cache占用的内存并不需要占用那么大，这个时候可以将其内存占比适当降低。怎么判断在什么时候调整RDD cache的内存占用比呢？其实通过Spark监控平台就可以看到Spark作业的运行情况了，如果发现task频繁的gc，就可以去调整cache的内存占用比了



### **堆外内存的调整**

方案:

--conf  spark.yarn.executor.memoryOverhead=2048

原因

有时候，如果你的spark作业处理的数据量特别特别大，几亿数据量；然后spark作业一运行就会出现类似shuffle file cannot find，executor、task lost，out of memory（内存溢出）等这样的错误。这是因为可能是说executor的堆外内存不太够用，导致executor在运行的过程中，可能会内存溢出；然后可能导致后续的stage的task在运行的时候，可能要从一些executor中去拉取shuffle map output文件，但是executor可能已经挂掉了，关联的blockmanager也没有了；所以可能会报shuffle  output file not found；resubmitting task；executor lost 这样的错误；最终导致spark作业彻底崩溃。



### **连接等待时长的调整**

方案:

--conf spark.core.connection.ack.wait.timeout=300

原因

 由于JVM内存过小，导致频繁的Minor gc，有时候更会触犯full gc，一旦出发full gc；此时所有程序暂停，导致无法建立网络连接；spark默认的网络连接的超时时长是60s；如果卡住60s都无法建立连接的话，那么就宣告失败了。碰到一种情况，有时候报错信息会出现一串类似file id not found，file lost的错误。这种情况下，很有可能是task需要处理的那份数据的executor在正在进行gc。所以拉取数据的时候，建立不了连接。然后超过默认60s以后，直接宣告失败。几次都拉取不到数据的话，可能会导致spark作业的崩溃。也可能会导致DAGScheduler，反复提交几次stage。TaskScheduler，反复提交几次task。大大延长我们的spark作业的运行时间。



原文  https://www.jianshu.com/p/e4557bf9186b