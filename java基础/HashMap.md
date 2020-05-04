### hashmap

```java
    public class HashMap<K,V> extends AbstractMap<K,V>
            implements Map<K,V>, Cloneable, Serializable {
        //存储数据的Node数组
        transient Node<K,V>[] table;
        //返回Map中所包含的Map.Entry<K,V>的Set视图。
        transient Set<Map.Entry<K,V>> entrySet;
        //当前存储元素的总个数
        transient int size;
        //HashMap内部结构发生变化的次数，主要用于迭代的快速失败（下面代码有分析此变量的作用）
        transient int modCount;
        //下次扩容的临界值，size>=threshold就会扩容，threshold等于capacity*load factor
        int threshold;
        //装载因子
        final float loadFactor;

        //默认装载因子
        static final float DEFAULT_LOAD_FACTOR = 0.75f;
        //由链表转换成红黑树的阈值TREEIFY_THRESHOLD
        static final int TREEIFY_THRESHOLD = 8;
        //由红黑树的阈值转换链表成UNTREEIFY_THRESHOLD
        static final int UNTREEIFY_THRESHOLD = 6;
        //默认容量（16）
        static final int DEFAULT_INITIAL_CAPACITY = 1 << 4; // aka 16
         //数组的最大容量 （1073741824）
        static final int MAXIMUM_CAPACITY = 1 << 30;
        //当桶中的bin(链表中的元素)被树化时最小的hash表容量。（如果没有达到这个阈值，即hash表容量小于MIN_TREEIFY_CAPACITY，当桶中bin的数量太多时会执行resize扩容操作）这个MIN_TREEIFY_CAPACITY的值至少是TREEIFY_THRESHOLD的4倍。
        static final int MIN_TREEIFY_CAPACITY = 64;
        略...
```

链表结构

```java
    static class Node<K,V> implements Map.Entry<K,V> {
        //hash
        final int hash;
        final K key;
        V value;
        Node<K,V> next;
        略...
```

红黑树结构

```java
    static final class TreeNode<K,V> extends LinkedHashMap.Entry<K,V> {
        TreeNode<K,V> parent;  // 父节点
        TreeNode<K,V> left;       //左节点
        TreeNode<K,V> right;     //右节点
        TreeNode<K,V> prev;    // needed to unlink next upon deletion
        boolean red;
```

put方法

```java
    public V put(K key, V value) {
        return putVal(hash(key), key, value, false, true);
    }

    final V putVal(int hash, K key, V value, boolean onlyIfAbsent,
                   boolean evict) {
        //p：链表节点  n:数组长度   i：链表所在数组中的索引坐标
        Node<K,V>[] tab; Node<K,V> p; int n, i;
        //判断tab[]数组是否为空或长度等于0，进行初始化扩容
        if ((tab = table) == null || (n = tab.length) == 0)
            n = (tab = resize()).length;
        //判断tab指定索引位置是否有元素，没有则，直接newNode赋值给tab[i]
        if ((p = tab[i = (n - 1) & hash]) == null)
            tab[i] = newNode(hash, key, value, null);
        //如果该数组位置存在Node
        else {
            //首先先去查找与待插入键值对key相同的Node，存储在e中，k是那个节点的key
            Node<K,V> e; K k;
            //判断key是否已经存在(hash和key都相等)
            if (p.hash == hash &&
                ((k = p.key) == key || (key != null && key.equals(k))))
                e = p;
            //如果Node是红黑二叉树，则执行树的插入操作
            else if (p instanceof TreeNode)
                e = ((TreeNode<K,V>)p).putTreeVal(this, tab, hash, key, value);
            //否则执行链表的插入操作（说明Hash值碰撞了，把Node加入到链表中）
            else {
                for (int binCount = 0; ; ++binCount) {
                    //如果该节点是尾节点，则进行添加操作
                    if ((e = p.next) == null) {
                        p.next = newNode(hash, key, value, null);
                        //判断如果链表长度，如果链表长度大于8则调用treeifyBin方法，判断是扩容还是把链表转换成红黑二叉树
                        if (binCount >= TREEIFY_THRESHOLD - 1) // -1 for 1st
                            treeifyBin(tab, hash);
                        break;
                    }
                    //如果键值存在，则退出循环
                    if (e.hash == hash &&
                        ((k = e.key) == key || (key != null && key.equals(k))))
                        break;
                    //把p执行p的子节点，开始下一次循环（p = e = p.next）
                    p = e;
                }
            }
            //在循环中判断e是否为null，如果为null则表示加了一个新节点，不是null则表示找到了hash、key都一致的Node。
            if (e != null) { // existing mapping for key
                V oldValue = e.value;
                //判断是否更新value值。（map提供putIfAbsent方法，如果key存在，不更新value，但是如果value==null任何情况下都更改此值）
                if (!onlyIfAbsent || oldValue == null)
                    e.value = value;
                //此方法是空方法，什么都没实现，用户可以根据需要进行覆盖
                afterNodeAccess(e);
                return oldValue;
            }
        }
        //只有插入了新节点才进行++modCount；
        ++modCount;
        //如果size>threshold则开始扩容（每次扩容原来的1倍）
        if (++size > threshold)
            resize();
        //此方法是空方法，什么都没实现，用户可以根据需要进行覆盖
        afterNodeInsertion(evict);
        return null;
    }
```



1.判断键值对数组tab[i]是否为空或为null，否则执行resize()进行扩容；

2.根据键值key计算hash值得到插入的数组索引i，如果table[i]==null，直接新建节点添加，转向6，如果table[i]不为空，转向3；

3.判断链表（或二叉树）的首个元素是否和key一样，不一样转向④，相同转向6；

4.判断链表（或二叉树）的首节点 是否为treeNode，即是否是红黑树，如果是红黑树，则直接在树中插入键值对，不是则执行5；

5.遍历链表，判断链表长度是否大于8，大于8的话把链表转换为红黑树（还判断数组长度是否小于64，如果小于只是扩容，不进行转换二叉树），在红黑树中执行插入操作，否则进行链表的插入操作；遍历过程中若发现key已经存在直接覆盖value即可；如果调用putIfAbsent方法插入，则不更新值（只更新值为null的元素）。

6.插入成功后，判断实际存在的键值对数量size是否超多了最大容量threshold，如果超过，进行扩容。



作者：jijs
链接：https://www.jianshu.com/p/b2d611c01bf3
来源：简书
著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。



### 个人对以上的总结:

扩容阈值: threshold = length * 0.75

threshold 是 HashMap 所能容纳的最大数据量的 Node(扩容的门栏),不到最大容量就会扩容.

length 是 table 的长度(初始值16)

size 是 HashMap 中**实际存在的键值对数量**



#### 存储形式

存储以 数组+ 链表+ 红黑树 

```java
Node 是 HashMap 的一个内部类，实现了 Map.Entry 接口，本质是就是一个映射 (键值对)
```

table数组的初始化长度是16.

#### put 添加数据

对 key 的 hashCode() 做 hash，然后再计算 index;

如果没碰撞直接放到 数组table 里；

如果碰撞了，以链表的形式存在 table[index] 后；

如果碰撞导致链表过长 (大于等于 8)，就把链表转换成红黑树；

如果节点已经存在就替换 old value(保证 key 的唯一性)

如果容量达到了扩容的阈值，就要 resize扩容。





h & (length-1)  相当于  取余的操作,这个节省性能.

#### 扩容

hashmap的size 大于扩容阈值时,扩容阈值= 数组长度 *  0.75 

大于 16*0.75=12 时，HashMap 会进行扩容的操作

数组的长度扩容为原来的1倍.

数组最大长度length:是1 << 30 

为什么是1 << 30 ? 因为左移31位就是负数了,int是32位整型,4字节. 首位是符号位 正数为0，负数为1

此时,扩容阈值(门栏) threshold = Integer.MAX_VALUE;



扩容时,复制旧map数据到新的map数据,关于数组索引,不用重新计算.

判断原来的 hash 值新增的那个 bit 是 1 还是 0 就好了，是 0 的话索引没变，是 1 的话索引变成 “原索引 + 旧table的长度”

resize 的过程，均匀的把之前的冲突的节点分散到新的 table[index]上 了





#### 使用entrySet取数据

可知，HashMap的遍历，是先遍历table，然后再遍历table上每一条单向链表，如上述的HashMap遍历出来的顺序就是Entry1、Entry2....Entry6，但显然，这不是插入的顺序，所以说：HashMap是无序的。

#### 对key为null的处理

其实key为null的put过程，跟普通key值的put过程很类似，区别在于key为null的hash为0，存放在table[0]的单向链表上而已。

key为null的取值，跟普通key的取值也很类似，只是不需要去算hash和确定存储在table上的index而已，而是直接遍历talbe[0]。

#### 删除

先查找,查找到了后,按照单链表的方式删除节点.



#### 为什么需要使用加载因子，为什么需要扩容呢？

HashMap 本来是以空间换时间,如果一直不进行扩容的话，链表就会越来越长，这样查找的效率很低.



#### HashMap 的 key 和 value 都能为 null 么？

如果 key 为 null，则直接从哈希表的第一个位置 table[0] 对应的链表上查找,由 putForNullKey（）实现



#### ConcurrentHashMap 线程安全.

get没有加锁.

只有put加锁了.

他是分段加锁的.对链表或者红黑树加锁.

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



### 红黑树

红黑树是一种特殊的二叉查找树

#### 什么是二叉查找树呢?

简单说就是一个有顺序的二叉树,左边 < 中间 < 右边  .

#### 什么是红黑树呢?

经过一些约定,让红黑树保证最长路径不超过最短路径的二倍，因而近似平衡。

