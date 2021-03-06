

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

#### JVM GC（垃圾回收机制）

在学习Java GC 之前，我们需要记住一个单词：stop-the-world 。它会在任何一种GC算法中发生。stop-the-world 意味着JVM因为需要执行GC而停止了应用程序的执行。当stop-the-world 发生时，除GC所需的线程外，所有的线程都进入等待状态，直到GC任务完成。GC优化很多时候就是减少stop-the-world 的发生。

#### GC

堆里面分为新生代和老年代，新生代包 含 Eden+Survivor 区，survivor 区里面分为 from 和 to 区，内存回收时，如果用的是复制算法，从 from 复制到 to，当经过一次或者多次 GC 之后，存活下来的对象会被移动
到老年区，当 JVM 内存不够用的时候，会触发 Full GC，清理 JVM 老年区
当新生区满了之后会触发 YGC,先把存活的对象放到其中一个 Survice
区，然后进行垃圾清理。因为如果仅仅清理需要删除的对象，这样会导致内存碎片，因此一般会把 Eden 进行完全的清理，然后整理内存。那么下次 GC 的时候， 就会使用下一个 Survive，这样循环使用。如果有特别大的对象，新生代放不下，
就会使用老年代的担保，直接放到老年代里面。因为 JVM 认为，一般大对象的存 活时间一般比较久远。

#### GC过程

每个空间的执行顺序如下：

1、绝大多数刚刚被创建的对象会存放在伊甸园空间（Eden）。

2、在伊甸园空间执行第一次GC（Minor GC）之后，存活的对象被移动到其中一个幸存者空间（Survivor）。

3、此后，每次伊甸园空间执行GC后，存活的对象会被堆积在同一个幸存者空间。

4、当一个幸存者空间饱和，还在存活的对象会被移动到另一个幸存者空间。然后会清空已经饱和的哪个幸存者空间。

5、在以上步骤中重复N次（N = MaxTenuringThreshold（年龄阀值设定，默认15））依然存活的对象，就会被移动到老年代。

从上面的步骤可以发现，两个幸存者空间，必须有一个是保持空的。如果两个两个幸存者空间都有数据，或两个空间都是空的，那一定是你的系统出现了某种错误。

我们需要重点记住的是，对象在刚刚被创建之后，是保存在伊甸园空间的（Eden）。那些长期存活的对象会经由幸存者空间（Survivor）转存到老年代空间（Old generation）。

也有例外出现，对于一些比较大的对象（需要分配一块比较大的连续内存空间）则直接进入到老年代。一般在Survivor 空间不足的情况下发生。

```
// 分配了一个又一个对象
放到Eden区
// 不好，Eden区满了，只能GC(新生代GC：Minor GC)了
把Eden区的存活对象copy到Survivor A区，然后清空Eden区（本来Survivor B区也需要清空的，不过本来就是空的）
// 又分配了一个又一个对象
放到Eden区
// 不好，Eden区又满了，只能GC(新生代GC：Minor GC)了
把Eden区和Survivor A区的存活对象copy到Survivor B区，然后清空Eden区和Survivor A区
// 又分配了一个又一个对象
放到Eden区
// 不好，Eden区又满了，只能GC(新生代GC：Minor GC)了
把Eden区和Survivor B区的存活对象copy到Survivor A区，然后清空Eden区和Survivor B区
// ...
// 有的对象来回在Survivor A区或者B区呆了比如15次，就被分配到老年代Old区
// 有的对象太大，超过了Eden区，直接被分配在Old区
// 有的存活对象，放不下Survivor区，也被分配到Old区
// ...
// 在某次Minor GC的过程中突然发现：
// 不好，老年代Old区也满了，这是一次大GC(老年代GC：Major GC)
Old区慢慢的整理一番，空间又够了
// 继续Minor GC
// ...
// ...
```



#### JVM GC什么时候执行？

eden区空间不够存放新对象的时候，执行Minro GC。升到老年代的对象大于老年代剩余空间的时候执行Full GC，或者小于的时候被HandlePromotionFailure 参数强制Full GC 。调优主要是减少 Full GC 的触发次数，可以通过 NewRatio 控制新生代转老年代的比例，通过MaxTenuringThreshold 设置对象进入老年代的年龄阀值（后面会介绍到）。



#### 各部分用的什么垃圾回收算法.

**新生代**:  采取Copying算法，因为新生代中每次垃圾回收都要回收大部分对象，也就是说需要复制的操作次数较少，采用Copying算法效率最高

**老年代**:每次回收都只回收少量对象，一般使用的是标记 整理算法



#### 内存溢出   内存泄露

内存溢出是指存储的数据超出了指定空间的大小

内存泄露:长生命周期对象A持有了短生命周期的对象B，那么只要A不脱离GC Root的链，那么B对象永远没有可能被回收，因此B就泄漏了。



#### GC的判定方法

引用链法： 通过一种 GC ROOT 的对象来判断，如果有一条链不能到达 GC ROOT 就说明可以回收

具体

#### GC的三种收集方法

标记清除:先标记，标记完毕之后再清除，效率不高，会产生碎片

复制算法：分为 8：1 的 Eden 区和 survivor 区，就是 YGC  

​		复制算法在存活对象比较少的时候，极为高效，但是带来的成本是牺牲一半的内存空间用于进行对象的移动

标记整理：标记完毕之后，让所有存活的对象向一端移动







#### **java 类加载机制**

虚拟机把描述类的数据从 Class 文件加载到内存，并对数据进行校验，解析和初始化，最终形成可以被虚拟机直接使用的 java 类型。

#### **类加载器双亲委派模型机制**

当一个类收到了类加载请求时，不会自己先去加载这个类，而是将其委派给父类，由父类去加载，如果此时父类不能加载，反馈给子类，由子类去完成类的加载

#### **什么是类加载器，类加载器有哪些**

通过类名获取该类的二进制字节流的代码块叫做类加载器。

主要有一下四种类加载器:

启动类加载器:	用来加载 java 核心类库，无法被 java 程序直接引用。

扩展类加载器:	它用来加载 Java 的扩展库。

系统类加载器：它根据 Java 应用的类路径（CLASSPATH） 来加载 Java 类。一般来说，Java 应用的类都是由它来完成加载的。

用户自定义类加载器，通过继承 java.lang.ClassLoader 类的方式实现。



### jvm优化

在spark中:

降低cache操作的内存占比 

增加Executor堆外内存,增加 (2G)

```
--conf  spark.yarn.executor.memoryOverhead=2048
```



增加连接等待时长,增加 300

```
--conf spark.core.connection.ack.wait.timeout=300
```



