[TOC]



###  1 if

基本语法

```shell
if [ 条件判断式 ];then 
  程序 
fi 


或者 

if [ 条件判断式 ] 
  then 
    程序 
elif [ 条件判断式 ]
	then
		程序
else
	程序
fi
```

**注意**

（1）[ 条件判断式 ]，中括号和条件判断式之间必须有空格

（2）if后要有空格

例子

vi if.sh

```shell
#!/bin/bash

if [ $1 -eq "1"  ];then
        echo "you input  1"
fi

echo "=============="
if [ $1 -eq "2" ]
then
        echo "you input 2"
elif [ $1 -eq "3" ]
then
        echo "you input 3"
fi
```

结果

```shell
(base) 192:code weijiqian$ ./if.sh 1
you input  1
==============
(base) 192:code weijiqian$ ./if.sh 2
==============
you input 2
(base) 192:code weijiqian$ ./if.sh 3
==============
you input 3
```



### case

1．基本语法

```shell
case $变量名 in 
  "值1"） 
    如果变量的值等于值1，则执行程序1 
    ;; 
  "值2"） 
    如果变量的值等于值2，则执行程序2 
    ;; 
  …省略其他分支… 
  *） 
    如果变量的值都不是以上的值，则执行此程序 
    ;; 
esac
注意事项：
1)case行尾必须为单词“in”，每一个模式匹配必须以右括号“）”结束。
2)双分号“;;”表示命令序列结束，相当于java中的break。
最后的“*）”表示默认模式，相当于java中的default。
```

例子

vi case.sh

```shell
#!/bin/bash
case $1 in
        "a")
        echo "u input a"
        ;;

        "b")
        echo "u input b"
        ;;

        *)
        echo "other"
        ;;
esac
```

结果

```shell
(base) 192:code weijiqian$ ./case.sh a
u input a
(base) 192:code weijiqian$ ./case.sh b
u input b
(base) 192:code weijiqian$ ./case.sh c
other
```



###  for

#### 基本语法1

```shell
	for (( 初始值;循环控制条件;变量变化 )) 
  do 
    程序 
  done
```

例子

```shell
#!/bin/bash

for((i=1;i<5;i++))
do
        echo "第$i 次循环"
done
```

结果

```shell
(base) 192:code weijiqian$ ./for1.sh
第1 次循环
第2 次循环
第3 次循环
第4 次循环
```

#### 基本语法2 

```shell
for 变量 in 值1 值2 值3… 
  do 
    程序 
  done
```

例子

```shell
#!/bin/bash
for i in "a  b  c  d"
do
        echo $i
done

```

结果

```shell
(base) 192:code weijiqian$ ./for2.sh
a b c d
```



### while

```shell
while [ 条件判断式 ] 
  do 
    程序
  done
```

例子

```shell
#!/bin/bash
s=0
i=0
while [ $i -lt 100 ]
do
    s=$[$s+$i]
    i=$[$i+1]
done
echo $s
```

结果

```shell
(base) 192:code weijiqian$ ./while.sh
4950
```

