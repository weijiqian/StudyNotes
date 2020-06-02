数据倾斜解决方法:



hive.map.aggr=true（用于设定是否在 map 端进行聚合，默认值为真，相当于combine） 

hive.groupby.mapaggr.checkinterval=100000（用于设定 map 端进行聚合操作的条数）

set hive.input.format= org.apache.hadoop.hive.ql.io.CombineHiveInputFormat;(在map执行前合并小文件，减少map数：CombineHiveInputFormat具有对小文件进行合并的功能（系统默认的格式）。HiveInputFormat没有对小文件合并功能)

set mapreduce.input.fileinputformat.split.maxsize=100 (增加map的方法为：根据computeSliteSize(Math.max(minSize,Math.min(maxSize,blocksize)))=blocksize=128M公式，调整maxSize最大值。让maxSize最大值低于blocksize就可以增加map的个数。)



设置reduce数

（1）每个Reduce处理的数据量默认是256MB

hive.exec.reducers.bytes.per.reducer=256000000

​	（2）每个任务最大的reduce数，默认为1009

hive.exec.reducers.max=1009

（3）计算reducer数的公式

N=min(参数2，总输入数据量/参数1)