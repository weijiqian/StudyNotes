###  1 概念

并发工具类

在 Java 5.0 提供了 **java.util.concurrent** (简称JUC )包，在此包中增加了在并发编程中很常用 的实用工具类，用于定义类似于线程的自定义子 系统，包括线程池、异步 IO 和轻量级任务框架。 提供可调的、灵活的线程池

### 2 什么是高并发

#### 并发:

​	单核CPU的情况下,多个线程操作同一个资源,单不是同时操作,是**交替操作**.

#### 并行

​	真正的多个线程**同时**执行,多核CPU,每个线程使用一个CPU的资源来运行.

#### 高并发

​	通过设计,让系统能够同时并行处理很多请求.



### 同步和异步的区别

​	同步，可以理解为在执行完一个函数或方法之后，一直等待系统返回值或消息，这时程序是出于阻塞的，只有接收到返回的值或消息后才往下执行其它的命令。

​    异步，执行完函数或方法后，不必阻塞性地等待返回值或消息，只需要向系统委托一个异步过程，那么当系统接收到返回值或消息时，系统会自动触发委托的异步过程，从而完成一个完整的流程。





### 3 高并发的设计

#### 1. 垂直扩展

增加硬件性能,

#### 2 水平扩展

​	集群



###  4 进程和线程

#### 	1 进程 

​		一个独立程序

#### 	2 线程

​		应用程序里面,程序员新增的Thread.



### 5 volatile 

修饰变量

当多个线程进行操作共享数据时，可以保证内存中的数据可见。

相较于 synchronized 是一种较为轻量级的同步策略

### 6 ConcurrentHashMap

见"数据结构与算法/HashMap"