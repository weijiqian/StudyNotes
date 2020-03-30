在Spark客户端安装包下的conf目录中创建文件hive-site.xml，配置hive的metastore路径

```xml
<configuration>
   <property>
        <name>hive.metastore.uris</name>
        <value>thrift://node01:9083</value>
   </property>
</configuration>
```

启动Hive的metastore服务
hive --service metastore 

启动zookeeper集群，启动Hadoop集群
启动SparkShell 读取Hive中的表总数，对比hive中查询同一表查询总数测试时间



# Spark操作Hive表数据，使用Intellij

spark2版本使用SparkSession作为统一入口，所以第一步就是给SparkSession增加Hive支持： enableHiveSupport（）

```
val spark = SparkSession
      .builder()
      .appName("Spark Hive Example").master("local[*]")
      .enableHiveSupport()
      .getOrCreate()

```



此外需要做的就是把在hive-site.xml文件中添加

```xml
<property> 
  <name>hive.metastore.uris</name> 
  <value>thrift://localhost:9083</value>
</property>
```

然后把hive-site.xml放在工程目录src/main/resources下，没有resoucres文件夹的可以新建一个。
使用前要记得启动

```
metastore service：hive --service metastore &
```





————————————————
版权声明：本文为CSDN博主「HeMJGaoMM」的原创文章，遵循 CC 4.0 BY-SA 版权协议，转载请附上原文出处链接及本声明。
原文链接：https://blog.csdn.net/weixin_38670967/article/details/86492164

————————————————
版权声明：本文为CSDN博主「SunnyRivers」的原创文章，遵循 CC 4.0 BY-SA 版权协议，转载请附上原文出处链接及本声明。
原文链接：https://blog.csdn.net/Android_xue/article/details/85157664