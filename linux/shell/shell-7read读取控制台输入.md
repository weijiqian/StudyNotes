基本语法

```shell

	read(选项)(参数)
	选项：
-p：指定读取值时的提示符；
-t：指定读取值时等待的时间（秒）。
参数
	变量：指定读取值的变量名
```

例子

```shell
#!/bin/bash

read -t 7 -p "Enter your name in 7 seconds " NAME
echo $NAME

(base) 192:code weijiqian$ ./read.sh 
Enter your name in 7 seconds xiaoze
xiaoze
```

