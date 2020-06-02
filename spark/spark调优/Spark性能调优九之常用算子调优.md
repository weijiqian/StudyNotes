### 1.使用mapPartitions算子提高性能

mapPartition的优点：使用普通的map操作，假设一个partition中有1万条数据，那么function就要被执行1万次，但是使用mapPartitions操作之后，function仅仅会被执行一次，显然性能得到了很大的提升，这个就没必要在多废话了。

mapPartition的缺点：使用普通的map操作，调用一次function执行一条数据，不会出现内存不够使用的情况；但是使用mapPartitions操作，很显然，如果数据量太过于大的时候，由于内存有限导致发生OOM，内存溢出。

总结：通过以上以上优缺点的对比，我们可以得出一个结论；就是在数据量不是很大的情况下使用mapPartition操作，性能可以得到一定的提升，在使用mapPartition前，我们需要预先估计一下每个partition的量和每个executor可以被分配到的内存资源。然后尝试去运行程序，如果程序没有问题就大可放心的使用即可，下图是一个实际的应用例子，仅供参考。



### 2.filter操作之后使用coalesce算子提高性能

经过一次filter操作以后，每个partition的数据量不同程度的变少了，这里就出现了一个问题；由于每个partition的数据量不一样，出现了数据倾斜的问题。比如上图中执行filter之后的第一个partition的数据量还有9000条。

解决方案：针对上述出现的问题，我们可以将filter操作之后的数据进行压缩处理；一方面减少partition的数量，从而减少task的数量；另一方面通过压缩处理之后，尽量让每个partition的数据量差不多，减少数据倾斜情况的出现，从而避免某个task运行速度特别慢。coalesce算子就是针对上述出现的问题的一个解决方案



### 3.使用foreachPartition算子进行

### 4.使用repartition解决SparkSQL低并行度的问题



在spark项目中，如果在某些地方使用了SparkSQL，那么使用了SparkSQL的那个stage的并行度就没有办法通过手动设置了，而是由程序自己决定。那么，我们通过什么样的手段来提高这些stage的并行度呢？其实解决这个问题的办法就是使partition的数量增多，从而间接的提高了task的并发度，要提高partition的数量，该怎么做呢？就是使用repartition算子，对SparkSQL查询出来的数据重新进行分区操作，此时可以增加分区的个数。



作者：z小赵
链接：https://www.jianshu.com/p/a1ca2ff91d9c
来源：简书
著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。