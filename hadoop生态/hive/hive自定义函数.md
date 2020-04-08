-- 自定义udf函数
step1: 继承UDF类，重写evaluate方法
step2: 打成jar包，hive执行add jar /***/***.jar;
step3: create temporary function 函数名称 as 'jar中类路径'



### 根据用户自定义函数类别分为以下三种：

​	（1）UDF（User-Defined-Function）

​		一进一出

​	（2）UDAF（User-Defined Aggregation Function）

​		聚集函数，多进一出

​		类似于：count/max/min

​	（3）UDTF（User-Defined Table-Generating Functions）

​		一进多出

​		如lateral view explore()