## Spark on Hive : (简单)

Hive只作为存储角色，Spark负责sql解析优化，执行。
这里可以理解为Spark 通过Spark SQL 使用Hive 语句操作Hive表 ,底层运行的还是 Spark RDD。具体步骤如下：

通过SparkSQL，加载Hive的配置文件，获取到Hive的元数据信息；
获取到Hive的元数据信息之后可以拿到Hive表的数据；
通过SparkSQL来操作Hive表中的数据。

## Hive on Spark：(难)



Hive既作为存储又负责sql的解析优化，Spark负责执行。
这里Hive的执行引擎变成了Spark，不再是MR，相较于Spark on Hive，这个实现较为麻烦，必须要重新编译spark并导入相关jar包。



## 目前，大部分使用Spark on Hive。

————————————————
版权声明：本文为CSDN博主「henrrywan」的原创文章，遵循 CC 4.0 BY-SA 版权协议，转载请附上原文出处链接及本声明。
原文链接：https://blog.csdn.net/henrrywan/article/details/86540486