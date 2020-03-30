#### spark 安装 、 和 hive on spark 安装配置

spark 和 hive 的版本有一定的要求,版本对应如下
hive-version   spark-version
 master              2.3.0
 3.0.x              2.3.0
 2.3.x              2.0.0
 2.2.x              1.6.0
 2.1.x              1.6.0
 2.0.x              1.5.0
 1.2.x              1.3.1
 1.1.x              1.2.0

### 1、准备工作
   1、java 1.8 安装及配置
   2、hadoop 2.7.7 安装及配置
   3、scala 2.11 安装及配置
   4、maven 3.6.1 安装及配置
   5、hive 2.3.5 安装及配置

   * 保证上述软件能够正常执行,并将其环境变量正确配置
   * 保证spark 集群环境可以实现无密码登录
   * 确保hadoop 集群可以正常使用
   * 确保hive 在spark 集群之前正常使用

注：

    hadoop-master   是我的主节点
    
    hadoop-slave1    是我的子节点
    
    hadoop-slave2    是我的子节点

### 2、下载并编译 spark-2.0.0 源码（这是必须的，不然无法安装hive on spark）

   2.1 解压源码到 /software/spark-2.0.0

   2.2 将源码进行编译并打包成可以分发的tar文件,利用spark 提供的  make-distribution.sh  
       2.2.1 设置maven 参数(该参数也可以设置到 /etc/profile 环境变量中,使其永久生效

```export MAVEN_OPTS="-Xmx2g -XX:MaxPermSize=512M -XX:ReservedCodeCacheSize=512m"```

​       2.2.2 正式开始编译之前,因为编译需要用到网络,而且会使用到maven的中心仓库,但是maven 的中心仓库属于国外网站,可能会很慢,所以这里先设置maven的中心仓库为国内的 aliyun 源



```````

编辑 /opt/maven-3.6.1/conf/settings.xml 文件
在 <mirrors></mirrors> 标签中添加如下内容
<mirror>
  <id>alimaven</id>
  <mirrorOf>central</mirrorOf>
  <name>aliyun maven</name>
  <url>http://maven.aliyun.com/nexus/content/groups/public/</url>
 </mirror>
```````

​       2.2.3 正式开始编译,进入解压好的spark-2.0.0目录下,执行如下命令,命令需要执行较长时间,请耐心等待,执行完毕后,目录下会有一个(编译的时候会使用部分内存,请保证内存在2G以上)

```
dev/make-distribution.sh --name "spark-2.0.0" --tgz -Pspark -Phadoop-2.7 -Pyarn

```

​     2.2.4 编译完成后,在当前目录下会生成一个 spark-2.0.0.tgz 文件(名字就是你2.2.3 设置的名称)
​             这里我生成了另外一个文件,具体原因不清楚,但是并没有其他影响只是文件名变了(spark-[WARNING] The requested profile "spark" could not be activated because it does not exist.-bin-spark-2.0.0.tgz)
​             手动做了修改,并将解压出来的目录也做了文件名的修改

### 3、配置 spark-2.0.0 
  #####  3.1 解压编译并打包好的spark-2.0.0 的源码(也就是spark-2.0.0.tgz)

```
tar -zxvf spark-2.0.0.tgz
# 移动到/opt目录下,这里只是方便管理
mv spark-2.0.0 /opt
       
       
   
```

3.2 配置spark-env.sh(spark-2.0.0/conf 目录下)



```
# 复制 spark-env.sh.template
cp spark-env.sh.template spark-env.sh
 
# 在末尾添加如下内容
export JAVA_HOME=/opt/jdk1.8.0_181
export SCALA_HOME=/opt/scala-2.11.0
export HADOOP_HOME=/opt/hadoop-2.7.7/
export HADOOP_CONF_DIR=/opt/hadoop-2.7.7/etc/hadoop
export YARN_CONF_DIR=/opt/hadoop-2.7.7/etc/hadoop
export SPARK_MASTER_IP=hadoop-master
export SPARK_WORKER_MEMORY=1g
export SPARK_WORKER_CORES=1
 
# 变量作用及解释
# JAVA_HOME 指向你自己的JAVA目录
# SCALA_HOME 指向你自己的SCALA目录
# HADOOP_HOME 指向你自己的HADOOP目录
# HADOOP_CONF_DIR 执行你自己的HADOOP的配置目录
# YANR_CONF_DIR 指向你自己的YARN配置目录
# SPARK_MASTER_IP SPARK主节点服务器地址,或者服务器名
# SPARK_WORKER_MEMORY SPARK工作节点使用的内存大小
# SPARK_WORKER_CORES SPARK工作节点的CPU核心数
```



   3.3 配置slaves 文件(spark-2.0.0/conf 目录下)
       

```
# 复制 slaves.template 文件
cp slaves.template slaves
 
# 在文件中添加你集群的所有服务器地址,或者服务器名(注意将原来的localhost 删除掉)
hadoop-master
hadoop-slave1
hadoop-slave2
 
# hadoop-master 是我spark集群的主节点名称
# hadoop-slave1 是我spark集群的工作节点名称
# hadoop-slave2 是我spark集群的工作节点名称
```



   3.4 配置完成后分发到工作节点(这里我用scp命令)

```
# 分发到工作节点1
scp -r /opt/spark-2.0.0 root@hadoop-slave1:/opt
 
# 分发到工作节点2
scp -r /opt/spark-2.0.0 root@hadoop-slave2:/opt
```



### 4、配置hive相关参数(hive 路径 /opt/hive-2.3.5)
   4.1 配置hive-env.sh 文件(hive-2.3.5/conf 目录下)

```
# 复制 hive-env.sh.template 
cp hive-env.sh.tempate hive-env.sh
 
# 编辑 hive-env.sh 文件,设置如下参数
# Set HADOOP_HOME to point to a specific hadoop install directory(hadoop 执行路径)
HADOOP_HOME=/opt/hadoop-2.7.7
 
# Hive Configuration Directory can be controlled by: (hive 配置路径)
export HIVE_CONF_DIR=/opt/hive-2.3.5/conf
```



   4.2 配置hive-site.xml 文件(hive-2.3.5/conf 目录下),配置较多,请耐心看完,description 标签为配置解释
        

```
<configuration>
    <property>
        <name>javax.jdo.option.ConnectionURL</name>
        <value>jdbc:mysql://localhost:3306/hive?createDatabaseIfNotExist=true</value>
        <description>hive元数据库的jdbc url,这里是mysql的元数据库</description>
    </property>
    <property>
        <name>javax.jdo.option.ConnectionDriverName</name>
        <value>com.mysql.jdbc.Driver</value>
        <description>指定元数据库驱动程序</description>
    </property>
    <property>
        <name>javax.jdo.option.ConnectionUserName</name>
        <value>root</value>
        <description>指定元数据库的用户名</description>
    </property>
    <property>
        <name>javax.jdo.option.ConnectionPassword</name>
        <value>tiger</value>
        <description>指定元数据库密码</description>
    </property>
    <property>
        <name>hive.metastore.schema.verification</name>
        <value>false</value>
        <description>这是强制hive升级是使用的元数据库参数,这里可以忽略</description>
    </property>
    <property>
        <name>hive.execution.engine</name>
        <value>spark</value>
        <description>执行hive以spark做为引擎 </description>
    </property>
    <property>
        <name>spark.master</name>
        <value>spark://hadoop-master:7077</value>
        <description>指定spark 集群路径</description>
    </property>
    <property>
        <name>spark.eventLog.enabled</name>
        <value>true</value>
        <description>是否记录spark事件,指定该参数后,必须指定spark.eventLog.dir</description>
    </property>
    <property>
        <name>spark.eventLog.dir</name>
        <value>hdfs://hadoop-master:9000/tmp/spark_log</value>
        <description>spark 事件路径,保证hdfs 文件系统上存在该路径</description>
    </property>
    <property>
        <name>spark.yarn.jars</name>
        <value>hdfs://hadoop-master:9000/lib/spark-jars/*</value>
        <description>配置spark的jar路径</description>
    </property>
    <property>
        <name>hive.spark.client.future.timeout</name>
        <value>60</value>
        <description>hive请求spark超时设置</description>
    </property>
    <property>
        <name>hive.spark.client.connect.timeout</name>
        <value>60000</value>
        <description>spark反馈hive超时设置,这个超时设置是因为虚拟机配置太差才设置的</description>
    </property>
</configuration>
```




   4.3 移动spark部分jar包到hive-2.3.5/lib 目录下

```
mv /opt/spark-2.0.0/jars/spark-network-common_2.11-2.0.0.jar /opt/hive/lib
mv /opt/spark-2.0.0/jars/spark-core_2.11-2.0.0.jar /opt/hive/lib
mv /opt/spark-2.0.0/jars/scala-library-2.11.8.jar /opt/hive/lib
```



   4.4 配置hive完成后,将hive 分发给spark 所有节点,因为spark每个节点上都需要用到相关的hivejar包(我在主节点上配置的,所以主节点不用分发了)

```
# 分发给工作节点1
scp -r /opt/hive-2.3.5 root@hadoop-slave1:/opt
 
# 分发给工作节点2
scp -r /opt/hive-2.3.5 root@hadoop-slave2:/opt
```




#### 5、其他配置
​     5.1 上传spark相关jar到hdfs指定路径(根据hive-site.xml 中 spark.yarn.jars 参数确定路径)

```
hdfs dfs -put /opt/spark-2.0.0/jars/* hdfs://hadoop-master:9000/lib/spark-jars
```


​     5.2 在hdfs文件系统中创建 spark 事件记录路径

```
hdfs dfs -mkdir -p hdfs://hadoop-master:9000/tmp/spark_log
```



6、启动spark,并访问web-ui

```
# 启动spark 集群
/opt/spark-2.0.0/sbin/start-all.sh
 
# 关闭spark 集群
/opt/spark-2.0.0/sbin/stop-all.sh
 
# web-ui 访问路径(默认路径如下)
http://hadoop-master:8080  
```



7、启动hive 并测试(hive 中已经事先有一张表了)

```
/opt/hive-2.3.5/bin/hive
 
# 切换到test 数据库
use test
 
# 执行count 操作
select count(1) from t_t1;
```

————————————————
版权声明：本文为CSDN博主「Iwg1021767001」的原创文章，遵循 CC 4.0 BY-SA 版权协议，转载请附上原文出处链接及本声明。
原文链接：https://blog.csdn.net/Iwg1021767001/article/details/90553225