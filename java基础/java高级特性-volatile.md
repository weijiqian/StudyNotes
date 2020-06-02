

### 为什么有volatile?

Java内存模型告诉我们，各个线程会将共享变量从主内存中拷贝到工作内存，然后执行引擎会基于工作内存中的数据进行操作处理。线程在工作内存进行操作后何时会写到主内存中？这个时机对普通变量是没有规定的.这个时候多线程操作的时候,就会出问题.

### volatile的作用

针对volatile修饰的变量给java虚拟机特殊的约定，线程对volatile变量的修改会立刻被其他线程所感知，即不会出现数据脏读的现象，从而保证数据的“可见性”。
通俗来说就是，线程A对一个volatile变量的修改，对于其它线程来说是可见的，即线程每次获取volatile变量的值都是最新的。

### volatile原理

为了提高处理速度，处理器不直接和内存进行通信，而是先将系统内存的数据读到内部缓存（L1，L2或其他）后再进行操作，但操作完不知道何时会写到内存。如果对声明了volatile的变量进行写操作，JVM就会向处理器发送一条Lock前缀的指令，将这个变量所在缓存行的数据写回到系统内存。但是，就算写回到内存，如果其他处理器缓存的值还是旧的，再执行计算操作就会有问题。所以，在多处理器下，为了保证各个处理器的缓存是一致的，就会实现缓存一致性协议，每个处理器通过嗅探在总线上传播的数据来检查自己缓存的值是不是过期了，当处理器发现自己缓存行对应的内存地址被修改，就会将当前处理器的缓存行设置成无效状态，当处理器对这个数据进行修改操作的时候，会重新从系统内存中把数据读到处理器缓存里。因此，经过分析我们可以得出如下结论：

Lock前缀的指令会引起处理器缓存写回内存；
一个处理器的缓存回写到内存会导致其他处理器的缓存失效；
当处理器发现本地缓存失效后，就会从内存中重读该变量数据，即可以获取当前最新值。

这样针对volatile变量通过这样的机制就使得每个线程都能获得该变量的最新值。

#### 我们在项目中如何使用？
1、状态标记量
在高并发的场景中，通过一个boolean类型的变量isopen，控制代码是否走促销逻辑，该如何实现？

```
public class ServerHandler {
    private volatile isopen;
    public void run() {
        if (isopen) {
           //isopen=true逻辑
        } else {
          //其他逻辑
        }
    }
    public void setIsopen(boolean isopen) {
        this.isopen = isopen
    }
}
```
场景细节无需过分纠结，这里只是举个例子说明volatile的使用方法，用户的请求线程执行run方法，如果需要开启促销活动，可以通过后台设置，具体实现可以发送一个请求，调用setIsopen方法并设置isopen为true，由于isopen是volatile修饰的，所以一经修改，其他线程都可以拿到isopen的最新值，用户请求就可以执行isopen=true的逻辑。

2、double check
单例模式的一种实现方式，但很多人会忽略volatile关键字，因为没有该关键字，程序也可以很好的运行，只不过代码的稳定性总不是100%，说不定在未来的某个时刻，隐藏的bug就出来了。
```java
class Singleton {
    private volatile static Singleton instance;
    public static Singleton getInstance() {
        if (instance == null) {
            syschronized(Singleton.class) {
                if (instance == null) {
                    instance = new Singleton();
                }
            }
        }
        return instance;
    } 
}
```


原文:https://github.com/wangzhiwubigdata/God-Of-BigData

