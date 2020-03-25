## 客户端

你可以用Java客户端做很多事情：

- 执行标准的index,get,delete,update,search等操作。
- 在正在运行的集群上执行管理任务。

但是，通过官方文档可以得知，现在存在至少三种Java客户端。

1. Transport Client
2. Java High Level REST Client
3. Java Low Level Rest Client

造成这种混乱的原因是：

- 长久以来，ES并没有官方的Java客户端，并且Java自身是可以简单支持ES的API的，于是就先做成了TransportClient。但是TransportClient的缺点是显而易见的，它没有使用RESTful风格的接口，而是二进制的方式传输数据。
- 之后ES官方推出了Java Low Level REST Client,它支持RESTful，用起来也不错。但是缺点也很明显，因为TransportClient的使用者把代码迁移到Low Level REST Client的工作量比较大。官方文档专门为迁移代码出了一堆文档来提供参考。
- 现在ES官方推出Java High Level REST Client,它是基于Java Low Level REST Client的封装，并且API接收参数和返回值和TransportClient是一样的，使得代码迁移变得容易并且支持了RESTful的风格，兼容了这两种客户端的优点。当然缺点是存在的，就是版本的问题。ES的小版本更新非常频繁，在最理想的情况下，客户端的版本要和ES的版本一致（至少主版本号一致），次版本号不一致的话，基本操作也许可以，但是新API就不支持了。
- 强烈建议ES5及其以后的版本使用Java High Level REST Client。笔者这里使用的是ES5.6.3，下面的文章将基于JDK1.8+ES7.3.2 Java High Level REST Client+Maven进行示例。

## Java High Level REST Client 介绍

Java High Level REST Client 是基于Java Low Level REST Client的，每个方法都可以是同步或者异步的。同步方法返回响应对象，而异步方法名以“async”结尾，并需要传入一个监听参数，来确保提醒是否有错误发生。

Java High Level REST Client需要Java1.8版本和ES。并且ES的版本要和客户端版本一致。和TransportClient接收的参数和返回值是一样的。

以下实践均是基于7.3.2的ES集群和Java High Level REST Client的