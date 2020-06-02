### 为什么有RPC?

在同一个程序里面,接口的提供方和调用方,都是在本地,直接调用.

但是在实际企业中,不同的接口在不同的服务器上面.要怎么调用?

这是问题,于是出现了远程调用接口.

### 如何调用他人的远程服务(接口)?

由于各服务部署在不同机器，服务间的调用免不了网络通信过程，服务消费方每调用一个服务都要写一坨网络通信相关的代码，不仅复杂而且极易出错。

如果有一种方式能让我们像调用本地服务一样调用远程服务，而让调用者对网络通信这些细节透明，那么将大大提高生产力，比如服务消费方在执行helloWorldService.sayHello("test")时，实质上调用的是远端的服务。这种方式其实就是RPC（Remote Procedure Call Protocol），在各大互联网公司中被广泛使用，如阿里巴巴的hsf、dubbo（开源）、Facebook的thrift（开源）、Google grpc（开源）、Twitter的finagle（开源）等。



### 流程

A把接口(例如getData())注册到RPC服务器上(例如Dubbo).

B去调用getData()接口,



### 原理





参考:https://blog.csdn.net/qq_26566331/article/details/71078208

