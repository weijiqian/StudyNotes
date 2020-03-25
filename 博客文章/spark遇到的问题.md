## 1 NoClassDefFoundError: org/apache/spark/sql/SparkSession

解决方法: 
pom文件里面将
<scope>provided</scope>去掉

