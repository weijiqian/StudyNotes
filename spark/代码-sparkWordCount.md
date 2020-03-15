## pom文件

```xml
<dependencies>
  <dependency>
    <groupId>org.apache.spark</groupId>
    <artifactId>spark-core_2.11</artifactId>
    <version>2.1.1</version>
  </dependency>
</dependencies>

<build>
<finalName>WordCount</finalName>
<plugins>
	<plugin>
		<groupId>net.alchim31.maven</groupId>
							<artifactId>scala-maven-plugin</artifactId>
                <version>3.2.2</version>
                <executions>
                    <execution>
                       <goals>
                          <goal>compile</goal>
                          <goal>testCompile</goal>
                       </goals>
                    </execution>
                 </executions>
            </plugin>
        </plugins>
</build>





```



## 2）编写代码

```scala
package com.atguigu

import org.apache.spark.{SparkConf, SparkContext}

object WordCount{

 def main(args: Array[String]): Unit = {

//1.创建SparkConf并设置App名称
  val conf = new SparkConf().setAppName("WC")

 

//2.创建SparkContext，该对象是提交Spark App的入口
  val sc = new SparkContext(conf)

  //3.使用sc创建RDD并执行相应的transformation和action
  sc.textFile(args(0)).flatMap(.split(" ")).map((, 1)).reduceByKey(+, 1).sortBy(.2, false).saveAsTextFile(args(1))

//4.关闭连接
  sc.stop()
 }
}

```





##  3）打包插件

```xml
<plugin>

        <groupId>org.apache.maven.plugins</groupId>

        <artifactId>maven-assembly-plugin</artifactId>

        <version>3.0.0</version>

        <configuration>

          <archive>

            <manifest>

              <mainClass>WordCount</mainClass>

            </manifest>

          </archive>

          <descriptorRefs>

            <descriptorRef>jar-with-dependencies</descriptorRef>

          </descriptorRefs>

        </configuration>

        <executions>

          <execution>

            <id>make-assembly</id>

            <phase>package</phase>

            <goals>

              <goal>single</goal>

            </goals>

          </execution>

        </executions>

   </plugin>



```



## 4）打包到集群测试

```shell
bin/spark-submit \

--class WordCount \

--master spark://hadoop102:7077 \

WordCount.jar \

/word.txt \

/out

```



