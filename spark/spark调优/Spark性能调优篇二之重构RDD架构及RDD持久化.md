如果一个RDD在两个地方用到,就持久化他.不然第二次用到他时,会再次计算.

直接调用cache()或者presist()方法对指定的RDD进行缓存（持久化）操作，同时在方法中指定缓存的策略。

原文:https://www.jianshu.com/p/9555644ccc0f