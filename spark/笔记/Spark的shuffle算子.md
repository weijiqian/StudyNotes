### 什么是shuffle?

shuffle过程，简单来说，就是将分布在集群中多个节点上的同一个key拉取到同一个节点上，进行聚合或join等操作。比如reduceByKey、join等算子，都会触发shuffle操作。

shuffle过程中，各个节点上的相同key都会先写入本地磁盘文件中，然后其它节点需要通过网络传输拉取各个节点上的磁盘文件中的相同key。而且相同key都拉取到同一个节点进行聚合操作时，还有可能会因为一个节点上处理的key过多，导致内存不够存放，进而溢写到磁盘文件中。因此在shuffle过程中，可能会发生大量的磁盘文件读写的IO操作，以及数据的网络传输操作。磁盘IO和网络数据传输也是shuffle性能较差的主要原因。

所以在我们的开发过程中，能避免则尽可能避免使用reduceByKey、join、distinct、repartition等会进行shuffle的算子，尽量使用map类的非shuffle算子。这样的话，没有shuffle操作或者仅有较少shuffle操作的Spark作业，可以大大减少性能开销。



### spark的shuffle算子

```
比如map,filter等算子,只关心自己分区的数据,不关心别人的数据,没有大局观,不是shuffle算子.
而sort排序,他需要全局排序,有大局观,就是shuffle算子.
总结:shuffle算子都是有大局观的算子.
```





一、去重

```
def distinct()



def distinct(numPartitions: Int)
```

二、聚合

```
def reduceByKey(func: (V, V) => V, numPartitions: Int): RDD[(K, V)]



def reduceByKey(partitioner: Partitioner, func: (V, V) => V): RDD[(K, V)]



def groupBy[K](f: T => K, p: Partitioner):RDD[(K, Iterable[V])]



def groupByKey(partitioner: Partitioner):RDD[(K, Iterable[V])]



def aggregateByKey[U: ClassTag](zeroValue: U, partitioner: Partitioner): RDD[(K, U)]



def aggregateByKey[U: ClassTag](zeroValue: U, numPartitions: Int): RDD[(K, U)]



def combineByKey[C](createCombiner: V => C, mergeValue: (C, V) => C, mergeCombiners: (C, C) => C): RDD[(K, C)]



def combineByKey[C](createCombiner: V => C, mergeValue: (C, V) => C, mergeCombiners: (C, C) => C, numPartitions: Int): RDD[(K, C)]



def combineByKey[C](createCombiner: V => C, mergeValue: (C, V) => C, mergeCombiners: (C, C) =>
```

 三、排序

```
def sortByKey(ascending: Boolean = true, numPartitions: Int = self.partitions.length): RDD[(K, V)]



def sortBy[K](f: (T) => K, ascending: Boolean = true, numPartitions: Int = this.partitions.length
```

四、重分区

```
def coalesce(numPartitions: Int, shuffle: Boolean = false, partitionCoalescer: Option[PartitionCoalescer] = Option.empty)



def repartition(numPartitions: Int)(implicit ord: Ordering[T] = null)
```

 

五、集合或者表操作

```
def intersection(other: RDD[T]): RDD[T]



def intersection(other: RDD[T], partitioner: Partitioner)(implicit ord: Ordering[T] = null): RDD[T]



def intersection(other: RDD[T], numPartitions: Int): RDD[T]



def subtract(other: RDD[T], numPartitions: Int): RDD[T]



def subtract(other: RDD[T], p: Partitioner)(implicit ord: Ordering[T] = null): RDD[T]



def subtractByKey[W: ClassTag](other: RDD[(K, W)]): RDD[(K, V)]



def subtractByKey[W: ClassTag](other: RDD[(K, W)], numPartitions: Int): RDD[(K, V)]



def subtractByKey[W: ClassTag](other: RDD[(K, W)], p: Partitioner): RDD[(K, V)]



def join[W](other: RDD[(K, W)], partitioner: Partitioner): RDD[(K, (V, W))]



def join[W](other: RDD[(K, W)]): RDD[(K, (V, W))]



def join[W](other: RDD[(K, W)], numPartitions: Int): RDD[(K, (V, W))]



def leftOuterJoin[W](other: RDD[(K, W)]): RDD[(K, (V, Option[W]))]
```

原文:https://blog.csdn.net/gegeyanxin/article/details/85038525