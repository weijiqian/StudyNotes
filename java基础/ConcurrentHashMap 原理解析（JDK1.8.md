```java
/**
    
     hash表初始化或扩容时的一个控制位标识量。
     负数代表正在进行初始化或扩容操作
     -1代表正在初始化
     -N 表示有N-1个线程正在进行扩容操作
     正数或0代表hash表还没有被初始化，这个数值表示初始化或下一次进行扩容的大小
     */
    private transient volatile int sizeCtl; 
    // 以下两个是用来控制扩容的时候 单线程进入的变量
    /**
     * The number of bits used for generation stamp in sizeCtl.
     * Must be at least 6 for 32bit arrays.
     */
    private static int RESIZE_STAMP_BITS = 16;
    /**
     * The bit shift for recording size stamp in sizeCtl.
     */
    private static final int RESIZE_STAMP_SHIFT = 32 - RESIZE_STAMP_BITS;
    
    
    /*
     * Encodings for Node hash fields. See above for explanation.
     */
    static final int MOVED     = -1; // hash值是-1，表示这是一个forwardNode节点
    static final int TREEBIN   = -2; // hash值是-2  表示这时一个TreeBin节点
```

```java
public V put(K key, V value) {
    return putVal(key, value, false);
}

    /** Implementation for put and putIfAbsent */
final V putVal(K key, V value, boolean onlyIfAbsent) {
    //ConcurrentHashMap 不允许插入null键，HashMap允许插入一个null键
    if (key == null || value == null) throw new NullPointerException();
    //计算key的hash值
    int hash = spread(key.hashCode());
    int binCount = 0;
    //for循环的作用：因为更新元素是使用CAS机制更新，需要不断的失败重试，直到成功为止。
    for (Node<K,V>[] tab = table;;) {
        // f：链表或红黑二叉树头结点，向链表中添加元素时，需要synchronized获取f的锁。
        Node<K,V> f; int n, i, fh;
        //判断Node[]数组是否初始化，没有则进行初始化操作
        if (tab == null || (n = tab.length) == 0)
            tab = initTable();
        //通过hash定位Node[]数组的索引坐标，是否有Node节点，如果没有则使用CAS进行添加（链表的头结点），添加失败则进入下次循环。
        else if ((f = tabAt(tab, i = (n - 1) & hash)) == null) {
            if (casTabAt(tab, i, null,
                         new Node<K,V>(hash, key, value, null)))
                break;                   // no lock when adding to empty bin
        }
        //检查到内部正在移动元素（Node[] 数组扩容）
        else if ((fh = f.hash) == MOVED)
            //帮助它扩容
            tab = helpTransfer(tab, f);
        else {
            V oldVal = null;
            //锁住链表或红黑二叉树的头结点
            synchronized (f) {
                //判断f是否是链表的头结点
                if (tabAt(tab, i) == f) {
                    //如果fh>=0 是链表节点
                    if (fh >= 0) {
                        binCount = 1;
                        //遍历链表所有节点
                        for (Node<K,V> e = f;; ++binCount) {
                            K ek;
                            //如果节点存在，则更新value
                            if (e.hash == hash &&
                                ((ek = e.key) == key ||
                                 (ek != null && key.equals(ek)))) {
                                oldVal = e.val;
                                if (!onlyIfAbsent)
                                    e.val = value;
                                break;
                            }
                            //不存在则在链表尾部添加新节点。
                            Node<K,V> pred = e;
                            if ((e = e.next) == null) {
                                pred.next = new Node<K,V>(hash, key,
                                                          value, null);
                                break;
                            }
                        }
                    }
                    //TreeBin是红黑二叉树节点
                    else if (f instanceof TreeBin) {
                        Node<K,V> p;
                        binCount = 2;
                        //添加树节点
                        if ((p = ((TreeBin<K,V>)f).putTreeVal(hash, key,
                                                      value)) != null) {
                            oldVal = p.val;
                            if (!onlyIfAbsent)
                                p.val = value;
                        }
                    }
                }
            }
            
            if (binCount != 0) {
                //如果链表长度已经达到临界值8 就需要把链表转换为树结构
                if (binCount >= TREEIFY_THRESHOLD)
                    treeifyBin(tab, i);
                if (oldVal != null)
                    return oldVal;
                break;
            }
        }
    }
    //将当前ConcurrentHashMap的size数量+1
    addCount(1L, binCount);
    return null;
}
```

1. 判断Node[]数组是否初始化，没有则进行初始化操作
2. 通过hash定位Node[]数组的索引坐标，是否有Node节点，如果没有则使用CAS进行添加（链表的头结点），添加失败则进入下次循环。
3. 检查到内部正在扩容，如果正在扩容，就帮助它一块扩容。
4. 如果f!=null，则使用synchronized锁住f元素（链表/红黑二叉树的头元素）
    4.1 如果是Node(链表结构)则执行链表的添加操作。
    4.2 如果是TreeNode(树型结果)则执行树添加操作。
5. 判断链表长度已经达到临界值8 就需要把链表转换为树结构。



总结：
    JDK8中的实现也是锁分离的思想，它把锁分的比segment（JDK1.5）更细一些，只要hash不冲突，就不会出现并发获得锁的情况。它首先使用无锁操作CAS插入头结点，如果插入失败，说明已经有别的线程插入头结点了，再次循环进行操作。如果头结点已经存在，则通过synchronized获得头结点锁，进行后续的操作。性能比segment分段锁又再次提升。



#### CAS  比较交换

CAS的全称是Compare And Swap 即比较交换

```
执行函数：CAS(V,E,N)
V表示要更新的变量

E表示预期值(V的原始值)

N表示新值

例子:
a=5;
线程1:把a改为6;
线程2:把a改为10;

线程1拿到变量a,原始值为5,准备更改a的值,此时,线程切换.
线程2开始执行,拿到变量a,原始值为5,此时,线程切换.
线程1执行,预期值为5,实际值为5,于是把a改为6,结束.线程切换
线程2执行,预期值为5,实际值为6,更改失败,结束.
在不加锁的情况下,安全的更改变量的值.

CAS 会出现ABA 问题.
```





作者：jijs
链接：https://www.jianshu.com/p/d10256f0ebea
来源：简书
著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。

### get操作不加锁的原因

#### get没有加锁的话，ConcurrentHashMap是如何保证读到的数据不是脏数据的呢？

```java
static class Node<K,V> implements Map.Entry<K,V> {
    final int hash;
    final K key;
    //可以看到这些都用了volatile修饰
    volatile V val;
    volatile Node<K,V> next;

    Node(int hash, K key, V val, Node<K,V> next) {
        this.hash = hash;
        this.key = key;
        this.val = val;
        this.next = next;
    }

```

因为  volatile

**Java提供了volatile关键字来保证可见性、有序性。但不保证原子性**

```
为什么有 volatile?
对于可见性，Java提供了volatile关键字来保证可见性、有序性。但不保证原子性。
普通的共享变量不能保证可见性，因为普通共享变量被修改之后，什么时候被写入主存是不确定的，当其他线程去读取时，此时内存中可能还是原来的旧值，因此无法保证可见性。
```

volatile的作用

- **总结下来**：
- 第一：使用volatile关键字会强制将修改的值立即写入主存；
- 第二：使用volatile关键字的话，当线程2进行修改时，会导致线程1的工作内存中缓存变量的缓存行无效（反映到硬件层的话，就是CPU的L1或者L2缓存中对应的缓存行无效）；
- 第三：由于线程1的工作内存中缓存变量的缓存行无效，所以线程1再次读取变量的值时会去主存读取。