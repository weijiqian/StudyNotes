### 系统函数

#### 1 basename基本语法

取出文件名

```shell
basename [string / pathname] [suffix]  	

选项：
suffix为后缀，如果suffix被指定了 就会删掉后缀名
```

例子

```shell
basename /home/linux/text.sh
text.sh

basename /home/linux/text.sh .sh
text
```

#### 2 dirname 

取出文件的绝对路径

```shell
dirname /home/linux/text.sh
/home/linux
```

###  自定义函数

1．基本语法

```shell

[ function ] funname[()]
{
	Action;
	[return int;]
}
funname
```

​	1 必须在调用函数地方之前，先声明函数，shell脚本是逐行运行。不会像其它语言一样先编译。

​	2 函数返回值，只能通过$?系统变量获得，可以显示加：return返回，如果不加，将以最后一条命令运行结果，作为返回值。return后跟数值n(0-255)

例子

```shell
#!/bin/bash
function sum()
{
  s=0
  s=$[$1+$2]
  echo $s
  return $1
}

sum 3 5
```

结果

```
(base) 192:code weijiqian$ ./fun.sh
8
```

