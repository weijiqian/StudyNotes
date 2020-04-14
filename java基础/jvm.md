

### 1 架构



![image-20200414151707216](./../image-md/jvm%E6%9E%B6%E6%9E%84%E5%9B%BE.png)

程序计数器: 当前执行命令的位置.

虚拟机栈: 正在运行的方法,会产生栈帧, 栈帧放入栈中.局部变量.

本地方法栈: 同上,native方法.

方法区:属于共享内存区域，存储已被虚拟机加载的类信息、常量、静态变量、即时编译器编译后的代码等数据

堆: 保存 对象实例,方法,常量.

​	分为 新生代,养老代,永久代.



![image-20200414154850605](../image-md/jvm%E4%B8%AA%E5%8C%BA%E5%9F%9F%E5%86%85%E5%AE%B9.png)

图来源 https://blog.csdn.net/qq_41701956/article/details/81664921





###  优化

```shell
堆大小
-Xms:初始堆大小，默认是物理内存的1/64。默认(MinHeapFreeRatio参数可以调整)空余堆内存小于40%时，JVM就会增大堆直到--Xmx的最大限制。例如：-Xms 20m。
-Xmx:最大堆大小。默认是物理内存的1/4  默认(MaxHeapFreeRatio参数可以调整)空余堆内存大于70%时，JVM会减少堆直到 -Xms的最小限制。

-XX:NewSize=n：设置年轻代大小（初始值）。 
-XX:MaxNewSize：设置年轻代最大值。
-XX:NewRatio=n:设置年轻代和年老代的比值。
-XX:SurvivorRatio=n:年轻代中Eden区与两个Survivor区的比值。
-XX:PermSize（1.8之后改为MetaspaceSize）  设置持久代(perm gen)初始值，默认是物理内存的1/64。
-XX:MaxPermSize=n:（1.8之后改为MaxMetaspaceSize）设置最大持久代大小。
-Xss：每个线程的堆栈大小。

```

