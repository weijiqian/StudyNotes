### hashmap

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



### 红黑树

红黑树是一种特殊的二叉查找树

#### 什么是二叉查找树呢?

简单说就是一个有顺序的二叉树,左边 < 中间 < 右边  .

#### 什么是红黑树呢?

经过一些约定,让红黑树保证最长路径不超过最短路径的二倍，因而近似平衡。



