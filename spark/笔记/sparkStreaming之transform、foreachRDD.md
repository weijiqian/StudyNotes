### transform：

每一批次调用一次
如下代码介绍的那样，foreachRDD内部最开始一部分是运行在Driver中，可以做一些连接之类的工作

### foreachRDD

**什么时候用foreachRDD,什么时候用transform？**

需要有返回时，就用transform。不需要返回时就用foreachRDD

**什么时候用 map,什么时候用 foreach？**

需要返回时用 map, 不需要返回时用 foreach
————————————————
版权声明：本文为CSDN博主「feiyuciuxun」的原创文章，遵循 CC 4.0 BY-SA 版权协议，转载请附上原文出处链接及本声明。
原文链接：https://blog.csdn.net/feiyuciuxun/article/details/103339005