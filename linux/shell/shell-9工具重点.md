### 1 cut 查询数据

从每一行中剪切数据

1.基本用法

cut [选项参数]  filename

说明：默认分隔符是制表符

2.选项参数说明

| 选项参数 | 功能                         |
| -------- | ---------------------------- |
| -f       | 列号，提取第几列             |
| -d       | 分隔符，按照指定分隔符分割列 |
| -c       | 指定具体的字符               |

3.案例实操

准备数据

```shell
cut.txt

11aaa 12aaaa 13aaa
21aaa 22aaaa 23aaa
31aaa 32aaaa 33aaa
```

取第一列

```shell
(base) 192:code weijiqian$ cut -d " " -f 1 cut.txt
11aaa
21aaa
31aaa
```

取第2,3 列

```shell
(base) 192:code weijiqian$ cut -d " " -f 2,3 cut.txt
12aaaa 13aaa
22aaaa 23aaa
32aaaa 33aaa
```



### 2 sed 增,删,改数据

对每一行数据进行增,删,改操作.

1. 基本用法

sed [选项参数]  ‘command’  filename

2. 选项参数说明

| 选项参数 | 功能                                  |
| -------- | ------------------------------------- |
| -e       | 直接在指令列模式上进行sed的动作编辑。 |
| -i       | 直接编辑文件                          |

3. 命令功能描述

| 命令  | 功能描述                              |
| ----- | ------------------------------------- |
| **a** | 新增，a的后面可以接字串，在下一行出现 |
| d     | 删除                                  |
| s     | 查找并替换                            |

4. 案例

```shell
cut.txt 

11aaa 12aaaa 13aaa
21aaa 22aaaa 23aaa
31aaa 32aaaa 33aaa
```

新增

```shell

[tony@hadoop1 data]$ sed -e 2a\newLine text.txt 
11aaa 12aaaa 13aaa
21aaa 22aaaa 23aaa
newLine
31aaa 32aaaa 33aaa

```

删除这一整行

```shell
(base) 192:code weijiqian$ sed '/11aaa/d' cut.txt
21aaa 22aaaa 23aaa
31aaa 32aaaa 33aaa
```

改

```shell
(base) 192:code weijiqian$ sed 's/11aaa/newaaa/g' cut.txt
newaaa 12aaaa 13aaa
21aaa 22aaaa 23aaa
31aaa 32aaaa 33aaa
```

### 3 awk

把文件逐行的读入，以空格为默认分隔符将每行切片，切开的部分再进行分析处理。

1. 基本用法

awk [选项参数] ‘pattern1{action1} pattern2{action2}...’ filename

pattern：表示AWK在数据中查找的内容，就是匹配模式

action：在找到匹配内容时所执行的一系列命令

2. 选项参数说明

表1-55

| 选项参数 | 功能                 |
| -------- | -------------------- |
| -F       | 指定输入文件折分隔符 |
| -v       | 赋值一个用户定义变量 |

3. 案例实操

   输出第1,3列

```shell
[tony@hadoop1 data]$ awk '{print $1,$3}' text.txt 
11aaa 13aaa
21aaa 23aaa
31aaa 33aaa
```

使用","分割

```
原始数据
11aaa,12aaaa,13aaa
21aaa,22aaaa,23aaa
31aaa,32aaaa,33aaa

[tony@hadoop1 data]$ awk -F, '{print $1,$2}' text2.txt 
11aaa 12aaaa
21aaa 22aaaa
31aaa 32aaaa


```

### 4 sort

它将文件进行排序，并将排序结果标准输出



```shell
a:40:5.4
b:20:4.2
c:50:2.3
d:10:3.5
e:30:1.6

```

按照第三列排序.

```shell
[tony@hadoop1 data]$ sort -t : -nrk 3  sort.sh
a:40:5.4
b:20:4.2
d:10:3.5
c:50:2.3
e:30:1.6

```

