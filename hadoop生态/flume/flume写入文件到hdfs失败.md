

### hadoop端口号



```
a1.sinks.k1.hdfs.path = hdfs://hadoop1:9000/eshop/

```

这里的值,要和core-site.xml中的一致才行.



### 缺少commons-io-2.4.jar

报错 

​	Caused by: java.lang.ClassNotFoundException: org.apache.commons.io.Charsets

解决:

​	下载 commons-io-2.4.jar 放入flume /lib下.

