

## RDD

RDD虽然以Person为类型参数，但是Spark框架并不了解Person类的内部结构



## DataFrame

DataFrame除了提供比RDD更丰富的算子之外，更重要的特点是提升执行效率、减少数据读取以及执行计划的优化。

DataFrame却提供了详细的结构信息，使得Spark Sql可以清楚地知道数据集中有哪些列，每列的名称和类型分别是什么。

DataFrame每一行的类型固定为Row，只有通过解析才能获取各个字段的值，无法直接获取每一列的值：

```scala
testDF.foreach{
  line =>
    val col1=line.getAs[String]("col1")
    val col2=line.getAs[String]("col2")

}
```



## DataSet

DataSet可以理解成DataFrame的一种特例，主要区别是DataSet每一个record存储的是一个强类型值而不是一个Row。



## 相同点

DataFrame与Dataset均支持sparksql的操作

![Jietu20200329-171640](/Users/weijiqian/Desktop/大数据/StudyNotes/image-md/Jietu20200329-171640.png)

## 不同点



DataSet是强类型的。比如可以有Dataset[Car]，Dataset[Person].

DataFrame也可以叫Dataset[Row],每一行的类型是Row

DataFrame不解析，每一行究竟有哪些字段，各个字段又是什么类型都无从得知，只能用上面提到的getAS方法或者共性中的拿出特定字段

而Dataset中，每一行是什么类型是不一定的，在自定义了case class之后可以很自由的获得每一行的信息

DataFrame只是知道字段，但是不知道字段的类型，所以在执行这些操作的时候是没办法在编译的时候检查是否类型失败的，比如你可以对一个String进行减法操作，在执行的时候才报错，而DataSet不仅仅知道字段，而且知道字段类型，所以有更严格的错误检查。就跟JSON对象和类对象之间的类比



在数据处理过程中，很少用到DataSet，处理文本文件使用第一种读取方式比较多，第二种读取方式一般用来读取parquet。