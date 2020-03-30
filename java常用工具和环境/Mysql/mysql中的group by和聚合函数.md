##### 启动与停止

- 启动mysql服务
  `sudo /usr/local/mysql/support-files/mysql.server start`
- 停止mysql服务
  `sudo /usr/local/mysql/support-files/mysql.server stop`
- 重启mysql服务
  `sudo /usr/local/mysql/support-files/mysql.server restart`
- 进入mysql目录文件
  `cd /usr/local/mysql/support-files`
- 进入mysql命令行
  `/usr/local/MySQL/bin/mysql -uroot -p12345678`
- 退出数据库
  `exit;`

##### 数据库相关操作

- 查询所有数据库
  `show databases;`
- 选择(使用)数据库
  `use mybatis;`
- 查询当前正在使用的数据库名称
  `select database();`
- 创建数据库
  `create database 数据库名称;`
  `创建数据库,判断不存在,再创建: create database if not exists 数据库名;`
- 删除数据库
  `drop database 数据库名称;`
  `判断数据库存在，存在再删除:drop database if exists 数据库名称;`

##### 数据库表相关操作

- 创建数据库表

```sql
create table 表名(
	列名1 数据类型1,
	列名2 数据类型2,
	....
	列名n 数据类型n
	);
```

- 复制表
  `create table 表名 like 被复制的表名;`
- 查看某个数据库中的所有的数据表
  `show tables;`
- 查看数据表的结构
  `desc pet;`或`describe pet;`
- 修改表名
  `alter table 表名 rename to 新的表名;`
- 修改表的字符集
  `alter table 表名 character set 字符集名称;`
- 添加一列
  `alter table 表名 add 列名 数据类型;`
- 删除列
  `alter table 表名 drop 列名;`
- 删除表
  `drop table 表名;`或`drop table if exists 表名 ;`
- 添加数据
  `insert into 表名(列名1,列名2,...列名n) values(值1,值2,...值n);`
  其中列名和值要一一对应。如果表名后，不定义列名，则默认给所有列添加值,如:`insert into 表名 values(值1,值2,...值n);`除了数字类型，其他类型需要使用引号(单双都可以)引起来.
- 删除数据
  `delete from 表名 where 条件`
  其中:如果不加条件，则删除表中所有记录。如果要删除所有记录, 使用`delete from 表名;`一般不推荐使用。这种操作有多少条记录就会执行多少次删除操作.
  `TRUNCATE TABLE 表名;`推荐使用，效率更高 先删除表，然后再创建一张一样的表.
- 修改数据
  `update 表名 set 列名1 = 值1, 列名2 = 值2,... where 条件;`如果不加任何条件，则会将表中所有记录全部修改.

```sql
insert into user2 values (1,'李四','123'); // 增
delete from pet where ower = 'disn'; //删
update pet set name = '后裔' where ower = 'dfn'; //改
```

- 查询数据

```sql
①> 、< 、<= 、>= 、= 、<>	
②BETWEEN...AND	
③ IN( 集合)	
④LIKE 模糊查询	
⑤_单个任意字符
⑥%多个任意字符
⑦IS NULL  
⑧and  或 &&
⑨or  或 || 
⑩not  或 !
查询条件应用举例:
SELECT * FROM user WHERE age >= 18;
SELECT * FROM user WHERE age >= 18 AND  age <=36;
SELECT * FROM user WHERE age BETWEEN 40 AND 70;
SELECT * FROM user WHERE age IN (6,18,37);
// 关于NULL
SELECT * FROM user WHERE height = NULL; 错误,因为null值不能使用=或（!=) 判断
SELECT * FROM user WHERE height IS NULL;(正确)
SELECT * FROM user WHERE height  IS NOT NULL;(正确)
// 查询姓陈的有哪些？< like>
SELECT * FROM user WHERE NAME LIKE '陈%';
// 查询姓名第二个字是新的人
SELECT * FROM user WHERE NAME LIKE "_新%";
// 查询姓名是三个字的人
SELECT * FROM user WHERE NAME LIKE '___';
// 查询姓名中包含狗的人
SELECT * FROM user WHERE NAME LIKE '%狗%';
```

##### 约束相关

- 主键约束 (primary key)
  能够唯一确定一张表中的的一条记录,我们通过给某个字段添加约束, 可以使得这个字段不重复且不为空.

```sql
 create table user (
	id int primary key auto_increment, // 在创建表时，添加主键约束，并且完成主键自增	
	name varchar(20)
 );
-- 联合主键: 由多个字段联合组成的主键, 只要联合的主键加起来不重复就可以.联合主键中的任何一个字段都不能为空.
create table user2 (
 	id int,
 	name varchar(20),
 	password varchar(20),
 	primary key(id, name)
);
```

表创建完成后:
添加主键.如:
①`alter table user add primary key(id);`

②`alter table user modify id int primary key;`
删除主键:`alter table user drop primary key;`

- 唯一约束：unique 约束修饰的字段的值不可以重复.

```sql
 create table user1 (
 	id int primary key auto_increment,
  	phone_num varchar(20) unique
  	 );
 create table user2 (
 	id int primary key auto_increment,
  	name varchar(20),
  	unique(id, name) // 表示两个字段在一起不重复就可以
  	 );
```

也可以在表创建完成后, 通过`alter table user3 add unique(phone_num);`或`alter table user3 modify phone_num varchar(20) unique;`来添加unique约束.
删除unique约束:`alter table user3 drop index phone_num;`

- 非空约束：not null 修饰的字段不能为空NULL

```java
create table user3 (
	id int primary key auto_increment,
	name varchar(20) not null
	);
```

删除非空约束:`alter table user3 modify name varchar(20);`

- 默认约束
  当我们插入字段值时候,如果对应的字段没有插入值,则会使用默认值.如果传入了值,则不会使用默认值.

```sql
create table user4(
	id int primary key auto_increment,
	age int default 18,
	name varchar(20) not null
	);
```

- 外键约束：foreign key

```sql
create table 表名(
....
外键列
constraint 外键名称 foreign key (外键列名称) references 主表名称(主表列名称)
);
// 班级
create table classes(
	id int primary key,
	name varchar(20)
	);	
// 学生表
create table student (
		id	int primary key,
		name varchar(20),
		class_id int,
		foreign key(class_id) references classes(id)
		);
```

##### 数据库查询进阶

- 查询所有记录
  例如:查询student表中的所有记录.
  `select * from student;`
- 查询指定字段
  例如:查询student中的sname,ssex,class.
  `select sname,ssex,class from student;`
- 查询教师表中所有的单位即不重复的depart列. <排除重复distinct>
  `select distinct depart from teacher;`
- 查询score表中成绩在60到80之间的所有记录 <查询区间 between…and…>
  `select * from score where degree between 60 and 80;`
  `select * from score where degree > 60 and degree < 80;`
- 查询score表中成绩为85,86或88的记录
  `select * from score where degree in(85, 86, 88);`
- 查询student表中’95031’班或性别为’女’的同学记录. <or 表示或者>
  `select *from student where class = '95031' or sex = '女';`
- 以class降序查询student表的所有记录 <降序:desc, 升序asc,默认升序(省略)>.
  `select * from student order by class desc;`
- 以cno升序,degree降序查询score表的所有记录
  `select * from score order by cno asc,degree desc;`
- 查询"95031’班的学生人数 <统计 count>
  `select count(*) from student where class = '95031';`
- 查询score表中最高分的学生学号和课程号(子查询)
  `select sno, cno from score where degree = (select max(degree) from score );`其中:select max(degree) from score 先查出最高分.
  `select sno,cno degree from score order by degree desc limit 0,1;`其中:limit第一个数字表示从多少开始,第二个表示多少条.当有多个相同最高分时,容易出bug,不推荐使用这种方式查询.
- 查询每门课的平均成绩
  `select cno, avg(degree) from score group by cno;`
- 查询score表中至少有2名学生选修的并以3开头的课程的平均分数.
  `select cno, avg(degree) from score group by cno having count(cno) >= 2 and cno like '3%';`
- 查询分数大于70, 小于90的sno列.
  `select sno, degree from score where degree between 70 and 90;`
- 查询所有学生的sname, cno和degree列.
  `select sname, cno, degree from student, score where student.sno = score.sno;`
- 查询所有学生的sno,cname和degree列
  `select sno,cname,degree from course ,score where course.cno = score.cno;`
- 查询"95031"班学生每门课的平均分.
  `select cno, avg(degree) from score where sno in (select sno from student where class = '95031') group by cno;`
- 查询选修"3-105"课程的成绩高于"109"号同学"3-105"成绩的所有同学的记录.
  `select * from score where cno = '3-105' and degree > (select degree from score where sno = '109' and cno = '3-105');`
- 查询成绩高于学号为"109", 课程号为"3-105"的成绩的所有记录
  `select * from score where degree > (select degree from score where sno = '109' and cno = '3-105');`
- 查询和学号为108,101的同学同年出生的所有的sno, sname, sbirthday
  `select *from student where year(sbirthday) in (select year(sbirthday) from student where sno in(108, 101));`
- 查询"张旭"教师任课的学生成绩
  `select * from score where cno = ( select cno from course where tno = (select tno from teacher where tname = "张旭"));`
- 查询选修某课程的同学人数多于5人的教师姓名.
  `select tname from teacher where tno = (select tno from course where cno = (select cno from score group by cno having count(*) > 5));`
- 查询存在有85分以上的成绩的课程的cno
  `select cno, degree from score where degree > 85;`
- 查询出"计算机系"教师所教课程的成绩表
  `select * from score where cno in (select cno from course where tno in (select tno from teacher where depart = "计算机系"));`
- 查询选修编号为"3-105"课程且成绩至少高于选休息编号为"3-245"的同学的cno,sno和degree,并按degree从高到低次序排序.
  `any 至少一个.`

```sql
select * from score where cno = '3-105' and degree > any(select degree from score where cno = '3-245') order by degree desc;
```

- 查询选修编号为"3-105"课程且成绩高于选休息编号为"3-245"的同学的cno,sno和degree,并按degree从高到低次序排序.
  `all 表示所有`

```sql
select * from score where cno = '3-105' and degree > all(select degree from score where cno = '3-245') order by degree desc;

```

  -   查询所有教师和同学的name, sex和birthday

```sql
- select tname as name, tsex as sex, tbirthday as birthday from teacher union select sname, ssex, sbirthday from student;

```



  -   查询所有"女"教师和"女"同学的name,sex和birthday

```sql
select tname as name, tsex as sex, tbirthday as birthday from teacher where tsex = '女' union select sname, ssex, sbirthday from student where ssex = '女';

```



  -   查询成绩比该课程成绩低的同学的成绩表
      思路: 从a表查出对应的分数跟b表筛选出来的平均分作比较.

```sql
select * from score a where degree < (select avg(degree) from score b where a.cno = b.cno);
表a
+-----+-------+--------+
| sno | cno   | degree |
+-----+-------+--------+
| 101 | 3-105 |     91 |
| 102 | 3-105 |     92 |
| 103 | 3-105 |     92 |
| 103 | 3-245 |     86 |
| 103 | 6-166 |     85 |
| 104 | 3-105 |     81 |
| 105 | 3-105 |     88 |
| 105 | 3-245 |     75 |
| 105 | 6-166 |     79 |
| 109 | 3-105 |     76 |
| 109 | 3-245 |     68 |
| 109 | 6-166 |     81 |
+-----+-------+--------+
12 rows in set (0.00 sec)   
```

- 查询所有任课教师的tname和depart
  `select tname, depart from teacher where tno in (select tno from course);`
- 查询至少有两名男生的班号

```sql
select class from student where ssex= '男' group by class having count(*) > 1

```

- 查询student表中不姓"王"的同学记录

```sql
select * from student where sname not like '王%';

```

- 查询student表中每个学生的姓名和年龄

```sql
select sname, year(now()) - year(sbirthday)  as '年龄' from student;

```

- 查询student表中最大和最小的sbirthday日期值

```sql
select max(sbirthday) as '最大', min(sbirthday) as '最小' from student;

```

- 以班号和年龄从大到小的顺序查询student表中的全部记录

```sql
select * from student order by class desc, sbirthday;
```

- 查询"男"教师及其所上的课程

```sql
select * from course where tno in (select tno from teacher where tsex = '男');
```

- 查询最高分同学的sno, cno和degree列

```sql
select * from score where degree = (select max(degree) from score);
```

- 查询和李军同性别的所有同学的sname

```sql
select sname from student where ssex = (select ssex from student where sname = '李军');
```

- 查询和李军同性别并同班 同学sname

```sql
select sname from student where ssex = (select ssex from student where sname = "李军") and class = (select class from student where sname = '李军');
```

- 查询所有选修"计算机导论"课程的"男"的成绩表

```sql
select * from score where cno = (select cno from course where cname = '计算机导论') and sno in(select sno from student where ssex = '男');
```

