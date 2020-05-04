### RDD

```
弹性分布式数据集

```

### Driver

```
销售,接业务.
```

### Executor

```
执行器.
项目中的程序员.
```

### 窄依赖

```
只有一个孩子
```

### 宽依赖

```
有多个孩子
```

### DAG

```
有向无环图.
```



### shuffle

```
比如map,filter等算子,只关心自己分区的数据,不关心别人的数据,没有大局观,不是shuffle算子.
而sort排序,他需要全局的数据进行排序,有大局观,就是shuffle算子.
总结:shuffle算子都是有大局观的算子.
```

### 累加器

```
一个数据分散到3台机器上计算.需要统计他们中的一个总数.
就需要派一个使者,去到各个机器上收集数据,再汇总.
```

### 广播变量

```
把一个大数据,通过广播告诉所有机器.
节省性能.
```



### spark运行流程

#### Standalone Cluster模式

```
spark集群----一个公司.腾讯
master----老板
worker----部门

driver---项目经理
execotur---执行器---程序员
application---自己编写的程序---客户提要求

流程:
任务提交后，
Master(老板)会找到一个Worker(部门)启动Driver(项目)进程
把application(客户的需求)提交给driver(项目经理),
driver(项目经理)去spark(公司)集群中找到master(老板)要资源,需要多部门配合.
master(老板)会根据调度算法找到可用的多个worker(部门),
Driver(项目经理)在worker(部门)中启动executor(程序员)程序.
Driver(项目经理)开始执行main函数，之后执行到Action算子时，开始划分stage，每个stage生成对应的taskSet，之后将task分发到各个Executor(程序员)上执行
```

#### Yarn Cluster 模式

```
Application---需求
ApplicationMaster(Driver)---项目经理
Executor---程序员

yarn---公司
ResourceManager---老板
NodeManager---部门


流程:
本地机器提交application(需求)到resourcemanager(老板),
resourcemanager(老板)在NodeManager(部门)上启动ApplicationManster(项目),就是driver.
ApplicationMaster(项目经理)向ResourceManager(老板)申请Executor(程序员)内存,
在合适的多个NodeManager(部门)上启动Executor(程序员)进程，
Driver(项目经理)开始执行main函数,
并根据宽依赖开始划分stage，每个stage生成对应的taskSet，之后将task分发到各个Executor(程序)上执行。

```

