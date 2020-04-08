[atguigu@hadoop102 bin]$ vim test.sh 

在文件中添加如下内容



```shell
#!/bin/bash

do_date=$1

 
echo '$do_date'

echo "$do_date"

echo "'$do_date'"

echo '"$do_date"'

echo date

```





2）查看执行结果

```shell
[atguigu@hadoop102 bin]$ test.sh 2019-02-10

$do_date

2019-02-10

'2019-02-10'

"$do_date"

2019年 05月 02日 星期四 21:02:08 CST

```



3）总结：

（1）单引号不取变量值

（2）双引号取变量值

（3）反引号`，执行引号中命令

（4）双引号内部嵌套单引号，取出变量值

（5）单引号内部嵌套双引号，不取出变量值