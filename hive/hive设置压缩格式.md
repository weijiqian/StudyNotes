方法一：在.sql脚本设置如下参数



```shell
set hive.exec.compress.intermediate=true --启用中间数据压缩
set hive.exec.compress.output=true; -- 启用最终数据输出压缩
set mapreduce.output.fileoutputformat.compress=true; --启用reduce输出压缩
set mapreduce.output.fileoutputformat.compress.codec=org.apache.hadoop.io.compress.SnappyCodec --设置reduce输出压缩格式
set mapreduce.map.output.compress=true; --启用map输入压缩
set mapreduce.map.output.compress.codec=org.apache.hadoop.io.compress.SnappyCodec；-- 设置map输出压缩格式
```

方法二：通过设置hive-site.xml文件设置启用中间数据压缩，配置文件如下：



```xml
       <property>
                <name>hive.exec.compress.intermediate</name>
                <value>true</value>
        </property>
        <property>
                <name>hive.exec.compress.output</name>
                <value>true</value>
        </property>
 <!-- map输出压缩 -->
        <property>
                <name>mapreduce.map.output.compress</name>
                <value>true</value>
        </property>
        <property>
                <name>mapreduce.map.output.compress.codec</name>
                <value>org.apache.hadoop.io.compress.SnappyCodec</value>
        </property>
        <!-- reduce输出压缩 -->
        <property>
                <name>mapreduce.output.fileoutputformat.compress</name>
                <value>true</value>
        </property>
        <property>
                <name>mapreduce.output.fileoutputformat.compress.codec</name>
                <value>org.apache.hadoop.io.compress.SnappyCodec</value>
        </property>
```