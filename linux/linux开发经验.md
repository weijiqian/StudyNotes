### 环境变量修改

1)修改/etc/profile 文件:用来设置系统环境参数，比如$PATH. 这里面的环境变量是对 系统内所有用户生效。使用 bash 命令，需要 source /etc/profile 一下。

2)修改~/.bashrc 文件:针对某一个特定的用户，环境变量的设置只对该用户自己有效。 使用 bash 命令，只要以该用户身份运行命令行就会读取该文件。



#### 查找进程并杀掉

```shell
ps -ef|grep zookeeper | grep -v "grep"
```



#### 对内容的统计

```shell
awk -F "," '{print $0}' salve.txt
```



#### 替换

```shell
sed -i 's/String/val/g' test.txt
把文件中的"String" 替换为 "val"
-i  替换后,写入文件
g   全部替换

```

