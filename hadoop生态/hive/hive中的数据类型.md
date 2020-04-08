### 原子数据类型

​	tinyint
​	smallint
​	int   ***
​	bigint   ***
​	boolean
​	double   ***
​	float   ***
​	string     'yy'   "yy"
​	timestamp



### 复杂数据类型:

​	array:
​		用于存放相同数据类型的一组数据
​		使用这种类型的时候  一定要给定泛型的  <泛型>



###		案例：

	-  array:

​		id	work_add
​		1	sh,gz,sz
​		2	bj,wh,xa
​		3	zz,jn,tj,hz
​		建表关联：
​		id int 
​		work_add:array<string>
​	

	建表语句：
	create table if not exists test_array(id int,work_add array<string>) row format delimited fields terminated by '\t' collection items terminated by ',';
			
	
	加载数据
		load data local inpath '/home/hadoop/tmpdata/test_arr' into table test_array;
	
	
	
	查询：
	select * from test_array;
	id		work_add
	1       ["sh","gz","sz"]
	2       ["bj","wh","xa"]
	3       ["zz","jn","tj","hz"]
	这里的数组也是通过索引访问 从0开始
	select id,work_add[0] from test_array;
	查询的时候  如果当前数组没有这个下标的数据  返回null
	
	fields terminated by '\t'指定列之间的分割符
	collection items terminated by ','  指定集合每一个元素之间的分割符
	指定分隔符的时候  规则 外--》内
- map
  	用于存放k-v 一组数据的集合  map中的每一个元素都是k-v
  	指定map集合时候  指定k  v的泛型 <>

  		collection items terminated by 
  		map keys terminated by 
  ​	

	数据：
	id	piaofang
	1	zsf:100,zfz:300
	2	lldq:23000,fcrs:20000,gf:300
	
	建表关联：
	id  int 
	piaofang map<string,int>
	建表语句
	create table if not exists test_map(id int,piaofang map<string,int>) row format delimited fields terminated by '\t' collection items terminated by ',' map keys terminated by ':' ;
	
	加载数据：
	load data local inpath '/home/hadoop/tmpdata/test_map' into table test_map;
	
	map keys terminated by ':' 指定map集合中每一个元素的k-v分隔符的
	
	查询：
	select * from test_map;
	id		piaofang
	1       {"zsf":100,"zfz":300}
	2       {"lldq":23000,"fcrs":20000,"gf":300}
	取数据  通过key取value
	select id,piaofang["zsf"] from test_map;
- struct 
  	java中的对象的结构   封装很多属性
  	每一条数据结构相同的   数据内部的每一个小字段代表的含义都一样

  ​		collection items terminated by 

		class Stu{
			string name;
			int age;
			string sex;
			string address;
		}
		数据：
		id	info
		1	zs,19,f,jx
		2	ls,18,m,bj
		3	ww,16,f,sz
		
	
	建表关联：
	id int 
	info struct<指定属性:类型>   多个属性之间 ，
	建表语句：
	create table if not exists test_struct(id int,info struct<name:string,age:int,sex:string,address:string>) row format delimited fields terminated by '\t' collection items terminated by ',';
	
	collection items terminated by  指定属性之间的分割符
	
	加载数据：
	load data local inpath '/home/hadoop/tmpdata/test_st' into table test_struct;
	
	查询：
	select * from test_struct;
	id	info
	1       {"name":"zs","age":19,"sex":"f","address":"jx"}
	2       {"name":"ls","age":18,"sex":"m","address":"bj"}
	3       {"name":"ww","age":16,"sex":"f","address":"sz"}
	
	访问：
	对象.属性
	select id,info.age from test_struct;

总结：
	array  字段有多个元素  每一个元素类型一致的
			每一条数据的这个字段的元素个数不一定一致的
			下标  0
	map   字段中有多个元素  每一个元素都是k-v
			[key]
	struct  字段中有多个元素  每一行字段的元素的个数一致的  对应位置的元素标识的含义一致的   规整的数据
			.属性



