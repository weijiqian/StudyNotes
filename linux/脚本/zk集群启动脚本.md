

zk.sh

```shell
#! /bin/bash
case $1 in
"start"){
for i in hadoop102 hadoop103 hadoop104
do
ssh $i "/opt/module/zookeeper-3.4.10/bin/zkServer.sh
start"
};; done
"stop"){
for i in hadoop102 hadoop103 hadoop104
do
ssh $i "/opt/module/zookeeper-3.4.10/bin/zkServer.sh
stop"
done
};;
"status"){
for i in hadoop102 hadoop103 hadoop104
do
ssh $i "/opt/module/zookeeper-3.4.10/bin/zkServer.sh
status"
done
};;
esac
```

```
chmod 777 zk.sh
```

使用

```
zk.sh start  //启动
zk.sh stop   //停止
```



