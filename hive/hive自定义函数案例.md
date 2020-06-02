[TOC]



### 编程步骤：

1. 继承org.apache.hadoop.hive.ql.UDF

2. 需要实现evaluate函数；evaluate函数支持重载；

3. 在hive的命令行窗口创建函数

   

   1. 添加jar

      ```
      add jar linux_jar_path
      ```

      

   2 . 创建function

      ```
      create [temporary] function [dbname.]function_name AS class_name;
      ```

      

   

4. 在hive的命令行窗口删除函数

	```
	Drop [temporary] function [if exists] [dbname.]function_name;
	```



​	**注意**   UDF必须要有返回类型，可以返回null，但是返回类型不能为void；

### 自定义UDF函数



- 1 maven配置

```xml
<dependencies>
		<!-- https://mvnrepository.com/artifact/org.apache.hive/hive-exec -->
		<dependency>
			<groupId>org.apache.hive</groupId>
			<artifactId>hive-exec</artifactId>
			<version>1.2.1</version>
		</dependency>
</dependencies>
```

- 2 编写代码

  ```java
  package com.atguigu.hive;
  import org.apache.hadoop.hive.ql.exec.UDF;
  
  public class Lower extends UDF {
  
    // s为输入数据
    //必须要有返回数据
  	public String evaluate (final String s) {
  		
  		if (s == null) {
  			return null;
  		}
      //在这里做你想做的操作,自由自在的.谁也看不到.
  		
  		return s.toLowerCase();
  	}
  }
  ```

  

- 3 打成jar包上传到服务器/opt/module/jars/udf.jar

- 4 将jar包添加到hive的classpath

  ```
  hive (default)> add jar /opt/module/datas/udf.jar;
  ```

  

- 5 创建临时函数与开发好的java class关联

  ```
  hive (default)> create temporary function mylower as "com.atguigu.hive.Lower";
  ```

  

- 6 ．即可在hql中使用自定义的函数strip 

  ```
  hive (default)> select ename, mylower(ename) lowername from emp;
  ```

  