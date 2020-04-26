

## window()

生成新的DStream

源码定义

```scala
def window(windowDuration: Duration): DStream[T] = window(windowDuration,this.slideDuration)

def window(windowDuration: Duration, slideDuration: Duration): DStream[T] = ssc.withScope {
    new WindowedDStream(this, windowDuration, slideDuration)
 }
```


两个重载方法，第一个只传窗口长度(滑动间隔默认为实例化ssc时的时间间隔)，第二个传窗口长度与滑动间隔

```scala
val ssc=new StreamingContext(sc,Seconds(1))//时间间隔为1s
val stream=xxxx //非重点，省略
 
stream.print()
stream.window(Seconds(4),Seconds(4)).print()
stream.window(Seconds(4),Seconds(5)).print()
```

第一次print():是每秒打印一次这1秒内接收的数据

第二次print():每4秒打印前4秒接收的数据

第三次print():每5秒打印最近4秒接收的数据 ，上个5秒间隔，第一秒内的数据不会打印



## reduceByWindow()

生成新的DStream，作用于key-value类型

```scala
  def reduceByWindow(
      reduceFunc: (T, T) => T,
      windowDuration: Duration,
      slideDuration: Duration
    ): DStream[T] = ssc.withScope {
    this.reduce(reduceFunc).window(windowDuration, slideDuration).reduce(reduceFunc)
  }
```

需要传3个参数，依次为reduce()方法，窗口长度，滑动长度。

该方法的主要过程是：将window内的数据调用reduce()算子聚合，生成新的DStream

```scala
val ssc=new StreamingContext(sc,Seconds(1))//时间间隔
val stream=xxxx //类型为DStream[String]
 
stream.print()
stream.reduce((s1,s2)=>{
    s1+":"+s2
}).print()
stream.reduceByWindow((s1,s2)=>{
    s1+":"+s2
},Seconds(60),Seconds(10)).print()
 
```

第一次print():每秒打印一次接收的数据

第二次print():每秒打印一次，会将每秒接收到的数据拼接(s1+":"+s2)起来 从开始一直到结束 

第三次print():每10秒打印一次，打印最近一分钟接收的数据，并拼接(s1+":"+s2)  最近一分钟的数据



## countByWindow()

统计这个window中元素的数量

```scala
 def countByWindow(
      windowDuration: Duration,
      slideDuration: Duration): DStream[Long] = ssc.withScope {
    //reduceByWindow()第二各方法_+_为逆函数
    this.map(_ => 1L).reduceByWindow(_ + _, _ - _, windowDuration, slideDuration)
  }
```

需要两个参数，依次为：窗口长度，滑动间隔

该方法的主要过程为:先将每个元素生成长整数1，然后调用reduceByWindow()算子，将每个元素值相加。

```scala
val ssc=new StreamingContext(sc,Seconds(1))//时间间隔为1s
val stream=xxxx
 
stream.print()
stream.count().print()
stream.countByWindow(Seconds(10),Seconds(2)).print()
```

第一次print():每秒打印一次接收的数据

第二次print():每秒打印一次接收到元素的数量

第三次print():每2秒打印一次最近10秒接收到元素的数量



## countByValueAndWindow()

统计每个元素的次数

```scala
   def countByValueAndWindow(
      windowDuration: Duration,
      slideDuration: Duration,
      numPartitions: Int = ssc.sc.defaultParallelism)
      (implicit ord: Ordering[T] = null)
      : DStream[(T, Long)] = ssc.withScope {
    this.map(x => (x, 1L)).reduceByKeyAndWindow(
      (x: Long, y: Long) => x + y,
      (x: Long, y: Long) => x - y,
      windowDuration,
      slideDuration,
      numPartitions,
      (x: (T, Long)) => x._2 != 0L
    )
  }
```

需要三个参数，依次为:窗口长度，滑动间隔，分区数（有默认值，可不传）

该方法的主要过程为：先将每个元素生成(元素,1L)，然后调用reduceByKeyAndWindow()，可以理解为按key聚合，统计每个key的次数，也就是统计每个元素的次数

```scala
val ssc=new StreamingContext(sc,Seconds(1))
val stream=xxxx
 
stream.print()
stream.countByValue().print()
data.countByValueAndWindow(Seconds(10),Seconds(2)).print()
```

第一次print():每秒打印一次接收的数据

第二次print():每秒打印一次，会统计每个元素的次数 

第三次print():每2秒打印最近10秒的数据，统计每个元素次数



## reduceByKeyAndWindow()

window 内 每个key的次数

```scala
def reduceByKeyAndWindow(
      reduceFunc: (V, V) => V,
      windowDuration: Duration
    ): DStream[(K, V)] = ssc.withScope {
    reduceByKeyAndWindow(reduceFunc, windowDuration, self.slideDuration, defaultPartitioner())
  }
```

该方法有6个重载方法，就不一一粘了，参数为：聚合函数，窗口长度

该方法主要过程为:调reduceByKey()函数

```scala
val ssc=new StreamingContext(sc,Seconds(1))
val stream=xxxx  //stream类型为[String,Long]
 
stream.print()
data.reduceByKey((c1,c2)=>{
    c1+c2
  }).print()
data.reduceByKeyAndWindow((c1,c2)=>{
    c1+c2
  },Seconds(10)).print()
```

第一次print():每秒打印一次接收的数据

第二次print():每秒打印一次，计算每个key的次数

第三次print():每秒打印一次最近10秒每个key的次数

## updatestateByKey()

所有的window操作都是计算长度为窗口长度的数据，非window操作都是计算设置的时间间隔内的数据，而updateBykey可以理解成在无限长的时间里，为每个key保存一个状态，这个时间长度可以通过ssc.awaitTerminationOrTimeout()来控制，一般来说长度每天或每小时。

```scala
def updateStateByKey[S: ClassTag](
      updateFunc: (Seq[V], Option[S]) => Option[S]
    ): DStream[(K, S)] = ssc.withScope {
    updateStateByKey(updateFunc, defaultPartitioner())
  }
```

当然，该方法重载方法也6个，这里只讨论上面的，传入一个更新方法，该方法两个参数：一个为当前时间间隔内数据，类型为Seq，一个为之前的数据，可能无数据(第一次提交计算的时候)，类型为Option，返回值也为Option类型

下面是两个实例，求pv和uv

```scala
//pv
val stream=xxxx//类型得转化为[当前日期，1L]
stream.updateStateByKey((curvalues:Seq[Long],prevalue:Option[Long])=>{
      val sum=curvalues.sum
      val pre=prevalue.getOrElse(0L)
      Some(sum+pre)
    })
```



```scala
//uv   因为uv涉及到去重，故将userid放入Set里
val stream=xxxx //类型为[当前日期，userid]
stream.updateStateByKey((curvalues:Seq[Set[String]],prevalue:Option[Set[String]])=>{
      var curs=Set[String]()
      if(!curvalues.isEmpty){
        curs=curvalues.reduce(_++_)//将两个Set集合合并
      }
      Some(curs++prevalue.getOrElse(Set[String]()))
    })
```

————————————————
版权声明：本文为CSDN博主「hadoop程序猿」的原创文章，遵循 CC 4.0 BY-SA 版权协议，转载请附上原文出处链接及本声明。
原文链接：https://blog.csdn.net/zhaolq1024/article/details/83749026