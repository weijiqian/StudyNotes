## interceptors   拦截器

​	可以拦截数据源  source 给数据源添加数据 header信息  为了后续的数据的更加方便的使用

默认拦截器有:

### 1）Timestamp Interceptor

​	在数据源上添加时间戳
​	headers:{timestamp=1554707017331}
​	key: timestamp 
​	value:当前系统的时间戳

 ###  2）host interceptor

​	拦截数据源  每一个event 在每一条数据的header中添加 hostname| ip 
​	key: host 
​	value : 当前主机的 hostname | ip 

例子

```
指定当前agent a1的 sources sinks  channels 的别名
a1.sources = r1
a1.sinks = k1
a1.channels = c1

agent的数据源的
a1.sources.r1.type = netcat
a1.sources.r1.bind = localhost
a1.sources.r1.port = 44444

指定agent的sink的
a1.sinks.k1.type = logger

指定agent的通道
a1.channels.c1.type = memory

指定拦截器
指定拦截器的别名
a1.sources.r1.interceptors = i1

指定拦截期的类型
a1.sources.r1.interceptors.i1.type = host

绑定agent的  r1   c1   k1 
a1.sources.r1.channels = c1
a1.sinks.k1.channel = c1




```

数据：
Event: { headers:{host=192.168.191.203} body: 68 65 6C 6C 6F 20 74 6F 6D 0D                   hello tom. }
使用：
%{host}



### 3）Static Interceptor

​	静态拦截器   拦截每一个event数据 手动定义拦截器的key value 手动在header中添加  需要添加的k v 便于后面的数据的分类使用
案例：

```
指定当前agent a1的 sources sinks  channels 的别名
a1.sources = r1
a1.sinks = k1
a1.channels = c1

agent的数据源的
a1.sources.r1.type = netcat
a1.sources.r1.bind = localhost
a1.sources.r1.port = 44444

定义拦截器
定义拦截器的别名
a1.sources.r1.interceptors = i1

定义拦截器的类型的
a1.sources.r1.interceptors.i1.type = static

手动指定拦截器的 key值
a1.sources.r1.interceptors.i1.key = class

手动指定拦截器的value值
a1.sources.r1.interceptors.i1.value = two

指定agent的sink的
a1.sinks.k1.type = logger

指定agent的通道
a1.channels.c1.type = memory

绑定agent的  r1   c1   k1 
a1.sources.r1.channels = c1
a1.sinks.k1.channel = c1




```

headers:{class=bd1811}



## 4）多个拦截器联合使用：

```
指定当前agent a1的 sources sinks  channels 的别名

a1.sources = r1
a1.sinks = k1
a1.channels = c1

agent的数据源的

a1.sources.r1.type = netcat
a1.sources.r1.bind = localhost
a1.sources.r1.port = 44444

定义拦截器

定义拦截器的别名

a1.sources.r1.interceptors = i1 i2

i1定义拦截器的类型的

a1.sources.r1.interceptors.i1.type = static

i1手动指定拦截器的 key值

a1.sources.r1.interceptors.i1.key = class

i1手动指定拦截器的value值

a1.sources.r1.interceptors.i1.value = bd1811

指定i2对应的拦截器

a1.sources.r1.interceptors.i2.type = timestamp

指定agent的sink的

a1.sinks.k1.type = logger

指定agent的通道

a1.channels.c1.type = memory

绑定agent的  r1   c1   k1 

a1.sources.r1.channels = c1
a1.sinks.k1.channel = c1




```



## 自定义拦截器

请见下文

[自定义flume拦截器-实现了多种功能](https://blog.csdn.net/u012443641/article/details/80757229)