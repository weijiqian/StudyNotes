### 1 基本语法

```shell
[ condition ]（注意condition前后要有空格）
注意：条件非空即为true，[ atguigu ]返回true，[] 返回false。
```



### 2 常用判断条件

​	1 两个整数之间比较

```
= 字符串比较
-lt 小于（less than）			-le 小于等于（less equal）
-eq 等于（equal）				-gt 大于（greater than）
-ge 大于等于（greater equal）	-ne 不等于（Not equal）
```

例子

```shell
(base) 192:code weijiqian$ [ 3 -lt 5 ]
(base) 192:code weijiqian$ echo $?
0
```

```shell
(base) 192:code weijiqian$ [ 3 -gt 5 ]
(base) 192:code weijiqian$ echo $?
1
```

​	2 按照文件权限进行判断

```
-r 有读的权限（read）			-w 有写的权限（write）
-x 有执行的权限（execute）
```

例子

```shell
(base) 192:code weijiqian$ [ -w comp.sh ]
(base) 192:code weijiqian$ echo $?
0
```

​	3 按照文件类型进行判断

```
-f 文件存在并且是一个常规的文件（file）
-e 文件存在（existence）		-d 文件存在并是一个目录（directory）
```

例子

```shell
(base) 192:code weijiqian$ [ -e comp.sh ]
(base) 192:code weijiqian$ echo $?
0
```

