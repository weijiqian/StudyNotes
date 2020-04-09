#### 1 Sort By：

​	分区内有序；

####  2 Order By：

全局排序，只有一个Reducer；

####  3  Distrbute By：

类似MR中Partition，进行分区，结合sort by使用。

####  4 Cluster By：

当Distribute by和Sorts by字段相同时，可以使用Cluster by方式。Cluster by除了具有Distribute by的功能外还兼具Sort by的功能。但是排序只能是升序排序，不能指定排序规则为ASC或者DESC。

