案例 

​	1::Toy Story (1995)::Animation|Children's|Comedy

hive默认不支持多字节分隔符  ||   ：：
解析的时候 默认按照单字节解析  :
1(mid)  : mname :Toy Story (1995) type

解析多字节  默认的方式不可以了

### 自定义解析库

1）自定义解析库  使用正则表达式的解析方式
org.apache.hadoop.hive.serde2.RegexSerDe
1::Toy Story 
2::
2）默认的输入   正则表达式
d :: (.*)
2::Jumanji (1995)::Adventure|Children's|Fantasy
(.*)::(.*)::(.*)
输入的多字节  ||   转义   \\\|\\\| 
()正则表达式的分组的  组编号从1开始的
3）默认的输出   正则解析的结果

建表语句：

```
create table movies(mid int,mname string,type string) 
row format serde 'org.apache.hadoop.hive.serde2.RegexSerDe'
with serdeproperties('input.regex'='(.)::(.)::(.*)','output.format.string'='%1$s %2$s %3$s') stored as textfile;

加载数据：
load data local inpath "/home/hadoop/tmpdata/movies.dat" into table movies;
```



```
95001||zs||男||19||IS
create table stu(id int,name string,sex string,age int,dept string) 
row format serde 'org.apache.hadoop.hive.serde2.RegexSerDe'
with serdeproperties('input.regex'='(.*)\\|\\|(.*)\\|\\|(.*)\\|\\|(.*)\\|\\|(.*)','output.format.string'='%1$s %2$s %3$s %4$s %5$s') stored as textfile;
```



