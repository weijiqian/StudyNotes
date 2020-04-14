### 1 下载

下载地址:https://flink.apache.org/downloads.html

下载集成hadoop的版本



### 2 配置

masters文件

```
hadoop1:8082
```

slaves文件

```
hadoop1
hadoop2
hadoop3

```

flink-conf.yaml

基础配置

|   参数   |   值   |  说明    |
| ---- | ---- | ---- |
|  jobmanager.rpc.address    |  hadoop001    |  jobmanager所在节点    |
| jobmanager.rpc.port | 6123 | jobManager端口，默认为6123 |
| jobmanager.heap.size | 2048m | jobmanager可用内存 |
| taskmanager.heap.size | 4096m | 每个TaskManager可用内存，根据集群情况指定 |
| taskmanager.numberOfTaskSlots | 3 | 每个taskmanager的并行度（5以内） |
| parallelism.default | 6 | 启动应用的默认并行度（该应用所使用总的CPU数） |
| rest.port | 8082 | Flink web UI默认端口与spark的端口8081冲突，更改为8082 |
|      |      |      |





​		