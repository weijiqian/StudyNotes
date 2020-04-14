###  1. 系统变量

​	常用变量

$HOME、$PWD、$SHELL、$USER等

查看系统变量的值

```shell
[atguigu@hadoop101 datas]$ echo $HOME
/home/atguigu
```



### 2. 自定义变量

#### 1．基本语法

（1）定义变量：变量=值 

（2）撤销变量：unset 变量

（3）声明静态变量：readonly变量，注意：不能unset



#### 2．变量定义规则

​	（1）变量名称可以由字母、数字和下划线组成，但是不能以数字开头，环境变量名建议大写。

​	（2）等号两侧不能有空格

​	（3）在bash中，变量默认类型都是字符串类型，无法直接进行数值运算。

​	（4）变量的值如果有空格，需要使用双引号或单引号括起来。

#### 3 引用

​	$变量名

#### 4 例子

```shell
#!/bin/bash
#定义变量A
A=5
echo $A   # 输出5


#给变量A重新赋值
A=8
echo $A  # 输出8  

#撤销变量A
unset A

#声明静态变量,不能改变
readonly B=3

#都是字符,不能计算
C=1+2
echo $C # 输出1+2

# 有空格的,要用引号
D="I LOVE U"
echo $D   # 输出 I LOVE  U

#export 把变量提升为全局环境变量
export F=hello



```



### 3. 特殊变量 $n

表示脚本的输入参数

$0  表示脚本名称

$1-$9表示 第1个到第9个参数

${10}  10以上的参数,要用大括号 

```shell
[atguigu@hadoop101 datas]$ touch parameter.sh 
[atguigu@hadoop101 datas]$ vim parameter.sh

#!/bin/bash
echo "$0  $1   $2"

[atguigu@hadoop101 datas]$ chmod 777 parameter.sh

[atguigu@hadoop101 datas]$ ./parameter.sh cls  xz
./parameter.sh  cls   xz
```



### 4. 特殊变量$#

输入参数的个数

```shell
[atguigu@hadoop101 datas]$ vim parameter.sh

#!/bin/bash
echo "$0  $1   $2"
echo $#

[atguigu@hadoop101 datas]$ chmod 777 parameter.sh

[atguigu@hadoop101 datas]$ ./parameter.sh cls  xz
parameter.sh cls xz 
2
```

###  5. 特殊变量$*  $@

$*	（功能描述：这个变量代表命令行中所有的参数，$*把所有的参数看成一个整体）

$@	（功能描述：这个变量也代表命令行中所有的参数，不过$@把每个参数区分对待）

### 6 特殊变量 $?

$？	（功能描述：最后一次执行的命令的返回状态。如果这个变量的值为0，证明上一个命令正确执行；如果这个变量的值为非0（具体是哪个数，由命令自己来决定），则证明上一个命令执行不正确了。）

例子

```shell
[atguigu@hadoop101 datas]$ ./helloworld.sh 
hello world
[atguigu@hadoop101 datas]$ echo $?
0    # 正常执行.
```

