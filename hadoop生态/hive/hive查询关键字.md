[TOC]

### 模板

select   join   where   group by order by having limit 
语句的顺序：
select ....from ...join...where...group by...having...order by...limit.....



### 1 join



注意：
	1）hive中支持等值连接  不支持非等值连接
	select * from a join b on a.id>b.id; (错的)
	2)hive支持多关联建的连接的  但是仅仅支持and的  不支持or的
	select * from a join b on a.id=b.id or a.name=b.name;(错的)
	3)支持多表连接的
join分类：

准备数据：

```

a
id	name
1	zs
2	ls
3	ww


b
id	score
1	30
2	70
4	0
```

建表：

```create table a(id int,name string) row format delimited fields terminated by '\t';
create table b(id int,score int) row format delimited fields terminated by '\t';
load data local inpath '/home/hadoop/tmpdata/a' into table a;
load data local inpath '/home/hadoop/tmpdata/b' into table b;
```

#### 1）内连接 inner join|join	

多个表的交集  两个表都有的数据关联上
	select 

	* 
	from a inner join b on a.id=b.id;
	结果：
	1       zs      1       30
	2       ls      2       70

#### 2) 外连接



左外连接left outer join|left join
	以join左侧的表为基准表  左侧表的数据一个不能少 右侧表填充关联的数据  填充不上  补充null

	
		select 
		* 
		from a left join b on a.id=b.id;
		结果：
		1       zs      1       30
		2       ls      2       70
		3       ww      NULL    NULL
	
#### 3) 右外连接 right outer join|right join

​	以join右侧表为基准表  右侧表数一个不能少

```

		select 
		* 
		from a right join b on a.id=b.id;
		结果：
		1       zs      1       30
		2       ls      2       70
		NULL    NULL    4       0	
```

#### 4 ) 	全外连接 full outer join | full join 

​	两个表的并集   两个表中有的关联建都会关联  能关联上则关联 关联不上  补充null

```
		select 
		* 
		from a full join b on a.id=b.id;
		结果：
		1       zs      1       30
		2       ls      2       70
		3       ww      NULL    NULL
		NULL    NULL    4       0

```

### 2 where 和  having

这两个和聚合函数一起使用的时候 
where group by  having 
where执行在聚合函数之前
	对聚合之前的数据做过滤的   在进行的聚合
	select 
	count(*) 
	from stu where age>20;
	每个部门中年龄大于19的人数
	select 
	dept,count(*) 
	from stu_managed where age>19 group by dept;
having执行聚合函数之后

过滤聚合结果的

```
举例：统计部门人数大于三个的有哪些部门
先统计每一个部门的人数（聚合） 选出人数》3
select 
dept,count(*) totalcount 
from stu_managed group by dept having totalcount>3;

每个部门中年龄大于19的 人数超过5个的有哪些部门
select 
dept,count(*) totalcount 
from stu_managed where age>19 group by dept having totalcount>5;

```



### 3 group by 分组

按照指定的字段将相同的分到一起
使用的时候容易出错
慎重选择select后面的字段
注意：
	1）group by执行顺序是在select之前的,group by 中不能使用select后面字段的别名的	
	select 
	dept d,count(*) totalcount 
	from stu_managed group by d;  错误的
	2）有group by   select后面的字段不能随便写的
	select后面只能跟两种形式的字段
		1）聚合函数
			select 
			dept,max(age) totalcount 
			from stu_managed group by dept;
		2）group by后面的字段
		select 
		dept,count(*) totalcount 
		from stu_managed group by dept;

### 4 order by | sort by |cluster by | distribute by 

stu_managed  按照年龄排序

- order by 全局排序
  对所有的reducetask结果数据一起排序做全局的排序
  select * from stu_managed order by age;

- sort by 局部排序 
  当只有一个reducetask的时候 sort by =order by  
  对每一个reducetask的执行结果进行排序
  每一个mapreduce分区中进行的排序
  select * from stu_managed sort by age;

  设定reducetask的个数：
  set mapreduce.job.reduces=3;

  每一个reducetask的数据是怎么分配的？
  	不是按照排序字段分的
  	每一个reducetask中的数据  每一次随机选取一个字段进行取hash值%reducetask的个数
  	sort by 擅长在每一个reducetask中进行排序

- distribute by 类似于指定mapreduce中的分区字段的
  		分桶|分区（mapreduce）
  	distribute by后面指定的分桶字段  指定完成分桶字段之后
  	分桶字段 .hash % reducetask 个数  分配数据到每一个桶中
  	这里的每一个桶----mapreduce中的每一个分区
  	distribute by 只指定分配数据到reducetask的标准 不进行排序的
  	一般情况下  distribute by(分桶) +sort by （局部排序）
  	distribute by最终分成几个桶 reducetask的个数决定的 手动设置的  不设置  默认1个
  	set mapreduce.job.reduces=3;
  	select * from stu_managed distribute by age sort by age;
  distribute by中数据的最终的分配原则：
  字段类型
  	整型   分桶字段 % reducetask个数
  	string  分桶字段.hash % reducetask个数
  select * from stu_managed distribute by name sort by age;

- cluster by :
  	先进行按照指定字段分  在在每一个reducetask中按照指定字段排序
  	=distribute by 字段 sort by 字段 （两个字段相同的）

  ```
  select 
  * 
  from stu_managed distribute by age sort by age;
  ===
  select 
  * 
  from stu_managed cluster by age;  只能升序
  当 distribute by 和 sort by字段一致的时候 升序 可以使用 cluster by 替换
  distribute by + sort by(指定排序规则的) 功能 > cluster by （只能升序）
  
  
  ```

### 5）limit 取的是全局的前几

分组求topN   每一个部门中年龄最大的前两个  不能使用limit
	

### 6）union | union all 

将两个查询结果  放在一起的
union 去重
union all 不去重的
select * from a   5
union 
select * from b;  6



### 注意：

- 1)hive中对in\exists 查询支持比较弱的
  select ...  from ..where id in ();
  hive2中支持的  hive1中不支持
  性能很差

- 2）select ... from ... where id=(select .... )   这种语法不支持

- 3）关联
  mysql中：关联两种实现
  select ...  from  a   join b on ...   推荐的
  select .... from .... where ..=..


======================================