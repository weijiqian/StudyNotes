### Hive中常用的存储格式

**1.1 textfile
**textfile为默认格式，存储方式为行存储。
**1.2 ORCFile
**hive/spark都支持这种存储格式，它存储的方式是采用数据按照行分块，每个块按照列存储，其中每个块都存储有一个索引。特点是数据压缩率非常高。

**1.3 Parquet
**Parquet也是一种行式存储，同时具有很好的压缩性能；同时可以减少大量的表扫描和反序列化的时间。

磁盘空间占用大小比较

**orc<parquet<textfile**

## Hive中压缩设置

#### 1. Hive中间数据压缩

hive.exec.compress.intermediate：默认该值为false，设置为true为激活中间数据压缩功能。HiveQL语句最终会被编译成Hadoop的Mapreduce job，开启Hive的中间数据压缩功能，就是在MapReduce的shuffle阶段对mapper产生的中间结果数据压缩。在这个阶段，优先选择一个低CPU开销的算法。 
  mapred.map.output.compression.codec：该参数是具体的压缩算法的配置参数，SnappyCodec比较适合在这种场景中编解码器，该算法会带来很好的压缩性能和较低的CPU开销。设置如下：

```
set hive.exec.compress.intermediate=true
set mapred.map.output.compression.codec= org.apache.hadoop.io.compress.SnappyCodec
```

#### 2. Hive最终数据压缩

  hive.exec.compress.output：用户可以对最终生成的Hive表的数据通常也需要压缩。该参数控制这一功能的激活与禁用，设置为true来声明将结果文件进行压缩。 
  mapred.output.compression.codec：将hive.exec.compress.output参数设置成true后，然后选择一个合适的编解码器，如选择SnappyCodec。设置如下：

```
set hive.exec.compress.output=true 
set mapred.output.compression.codec=org.apache.hadoop.io.compress.SnappyCodec
```



### 常用orc + snappy