### 内部表：
​		hive自己进行管理表中数据
​		hive表中的数据存储的hdfs的 对应的一个hdfs的目录
​		hive自己管理hdfs的目录   绝对删除权限的
​		hive在进行删除表数据的数据的时候  连同hdfs的对应的表数据的目录一并删除的

	### 外部表：

​		外部表存储hdfs的一个目录的
​		hive对hdfs的数据只有使用权限  没有删除权限的
​		对于外部表来说  表删除的时候只能删除表结构信息（元数据）不会删除表中的数据（hdfs数据不会动）

	##### 内部表和外部表的本质区别：

​		删除表的时候  内部表表中数据和元数据一并删除
​		外部表  只会删除元数据   表中数据不会被删除的	
​	



功能：

	### 分区表

```
创建分区表
create table mingxing_ptn(id int, name string, sex string, age int, department string) partitioned by (city string) row format delimited fields terminated by ',';
```



​		将原始表划分成多个目录
​		为了提升查询性能  将原始表创建为分区表
​		不同于mapreduce中的分区
​		hive中表中的数据存储在hdfs的   hive中存储的数据可能很大
​		在进行表查询的时候  每次查询 都会进行hive的全表扫描
​		select * from ....
​		表中数据很大（100T） 全表扫描  极大的降低查询性能
​		可以对原始表进行分成不同的区域，分成多个‘小表’，每次查询的时候 可以指定查询某一个|几个小表   不用进行全表扫描了   提升查询性能
​		这里的每一个区域|‘小表’----一个分区
​		这个表---就叫做分区表

​	分区字段  address
​		就相当于在原来的目录下创建子目录
​		/user/hadoop/test.db/stu/address=beijing
​		/user/hadoop/test.db/stu/address=shanghai
​		/user/hadoop/test.db/stu/address=wuhan
​		select * from stu where address=wuhan

这时候只扫描/user/hadoop/test.db/stu/address=wuhan所有的文件
		每一个分区  这里对应的就是一个子目录	



### 分桶表：

```
创建分桶表
create table mingxing_bck(id int, name string, sex string, age int, department string) clustered by(id) sorted by(age desc) into 4 buckets row format delimited fields terminated by ',';
注意：clustered里的字段必须要是表字段中出现的字段
分桶字段
表字段
分桶字段和排序字段可以不一样，分桶字段和排序字段都必须是表字段中的一部分
分桶的原理和MapReduce的HashPartitioner的原理一致
```



​	mapreduce中的分区的概念
​	将原始表划分成多个文件（多个桶表）
​	作用：
​		1）提升数据抽样的性能
​			原始数据很大   样本数据具备代表性
​			散列的数据
​			原始数据抽  很多次
​			分桶表  桶表--mapreduce中的一个分区
​			mapkey.hash% 桶表的个数
​			每一个桶表数据  都是随机的  散列的数据
​			一个桶的数据  作为一个样本数据
​		2）提升join的性能
​	分桶表   核心
​		分桶字段   分桶依据(表中的某一个字段)  mapreduce--mapkey  需求
​		桶的个数   mapreduce--reducetask的个数  手动指定
​		每一个桶的数据
​			分桶字段.hash  % 分桶个数
​			分桶字段 整型的 int long 分桶字段%分桶个数
​					字符串   分桶字段.hash  % 分桶个数
​		select * from a join b on a.id=b.id;
​		分桶字段  id
​		个数  a  3   b--3|6|9
​	分桶表的表现形式：
​		每一个分桶表对应一个文件
​		分几个桶  数据对应几个文件
​		/user/hadoop/test.db/stu
​		age   3
​		/user/hadoop/test.db/stu/part-r-00000  age%3=0
​		/user/hadoop/test.db/stu/part-r-00001  age%3=1
​		/user/hadoop/test.db/stu/part-r-00002  age%3=2
没有一个表 即是内部表 又是外部表的
一个表可能即是分区表  又是分桶表
建表的时候 肯定先指定表权限 内部表|外部表 在指定 分区表|分桶表