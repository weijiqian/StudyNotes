

### 1  task的内存缓冲调节参数

### 2 reduce端聚合内存占比



```
spark.shuffle.file.buffer                     map task的内存缓冲调节参数，默认是32kb

spark.shuffle.memoryFraction          reduce端聚合内存占比，默认0.2

```

怎么判断在什么时候对这两个参数进行调整呢？

通过监控平台查看每个executor的task的shuffle write和shuffle read的运行次数，如果发现这个指标的运行次数比较多，那么就应该考虑这两个参数的调整了；这个参数调整有一个前提，spark.shuffle.file.buffer参数每次扩大一倍的方式进行调整，spark.shuffle.memoryFraction参数每次增加0.1进行调整。



### shuffle产生大量文件

为了解决shuffle产生大量文件的问题，我们可以在map端输出的位置，将文件进行合并操作，即使用

**spark.shuffle.consolidateFiles** 参数来合并文件，具体的使用方式为 

> new SparkConf().set("spark.shuffle.consolidateFiles","true")





作者：z小赵
链接：https://www.jianshu.com/p/069c37aad295
来源：简书
著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。