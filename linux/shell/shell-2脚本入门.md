###  1 hello world

```shell
[atguigu@hadoop101 datas]$ touch helloworld.sh
[atguigu@hadoop101 datas]$ vi helloworld.sh

在helloworld.sh中输入如下内容
#!/bin/bash
echo "helloworld"
```

### 2 执行方式

#### 2.1 采用bash或sh

采用bash或sh+脚本的相对路径或绝对路径（不用赋予脚本+x权限）

```shell
[atguigu@hadoop101 datas]$ sh helloworld.sh 
Helloworld
```

```shell
[atguigu@hadoop101 datas]$ sh /home/atguigu/datas/helloworld.sh 
helloworld
```

```shell
[atguigu@hadoop101 datas]$ bash helloworld.sh 
Helloworld
```

```shell
[atguigu@hadoop101 datas]$ bash /home/atguigu/datas/helloworld.sh 
Helloworld
```

#### 2.2 采用输入脚本的绝对路径或相对路径执行脚本

采用输入脚本的绝对路径或相对路径执行脚本（必须具有可执行权限+x）

首先赋予执行权限

```shell
[atguigu@hadoop101 datas]$ chmod 777 helloworld.sh
```

执行脚本

```shell
[atguigu@hadoop101 datas]$ chmod 777 helloworld.sh
```

```shell
[atguigu@hadoop101 datas]$ /home/atguigu/datas/helloworld.sh 
Helloworld
```

注意：第一种执行方法，本质是bash解析器帮你执行脚本，所以脚本本身不需要执行权限。第二种执行方法，本质是脚本需要自己执行，所以需要执行权限。