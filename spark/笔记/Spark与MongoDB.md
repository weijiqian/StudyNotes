maven配置

```xml
 <dependencies>
       
        <!-- 导入scala的依赖 -->
        <dependency>
            <groupId>org.scala-lang</groupId>
            <artifactId>scala-library</artifactId>
            <version>${scala.version}</version>
        </dependency>
 
        <!-- 导入spark的依赖 -->
        <dependency>
            <groupId>org.apache.spark</groupId>
            <artifactId>spark-core_2.11</artifactId>
            <version>${spark.version}</version>
        </dependency>
 
 
        <!-- 导入spark sql的依赖 -->
        <dependency>
            <groupId>org.apache.spark</groupId>
            <artifactId>spark-sql_2.11</artifactId>
            <version>${spark.version}</version>
        </dependency>
 
        <dependency>
            <groupId>org.mongodb.spark</groupId>
            <artifactId>mongo-spark-connector_2.11</artifactId>
            <version>2.2.0</version>
        </dependency>
 
    </dependencies>
```





## 读取数据

```scala
import com.mongodb.spark.MongoSpark
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.{DataFrame, Row}
 
/**
  * created by LMR on 2019/6/12
  */
object DataPreProcess {
 
  Logger.getLogger("org").setLevel(Level.ERROR)
  def main(args: Array[String]): Unit = {
 
    import org.apache.spark.sql.SparkSession
 
    val spark = SparkSession.builder()
      .master("local")
      .appName("MongoSparkConnectorIntro")
      .config("spark.mongodb.input.uri", "mongodb://192.168.177.13/novels.novel")
      .getOrCreate()
 
    import spark.implicits._
    val frame: DataFrame = MongoSpark.load(spark)
    frame.createTempView("nolves")
    val res: DataFrame = spark.sql("SELECT bookname, author, category from nolves limit 10")
    res.show()
    
  }
 
}
```



## 读取数据,使用Schema约束

```scala
package com.mongodb.spark

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}


object ReadMongoSchema {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .master("local")
      .appName("MyApp")
      .config("spark.mongodb.input.uri", "mongodb://127.0.0.1/test.user")
      .getOrCreate()

    // 设置log级别
    spark.sparkContext.setLogLevel("WARN")

    val schema = StructType(
      List(
        StructField("name", StringType),
        StructField("age", IntegerType),
        StructField("sex", StringType)
      )
    )

    // 通过schema约束，直接获取需要的字段
    val df = spark.read.format("com.mongodb.spark.sql").schema(schema).load()
    df.show()

    df.createOrReplaceTempView("user")

    val resDf = spark.sql("select * from user")
    resDf.show()

    spark.stop()
    System.exit(0)
  }
}



```

## 写入mongodb数据

```scala
package com.mongodb.spark

import org.apache.spark.sql.SparkSession
import org.bson.Document

object WriteMongo {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .master("local")
      .appName("MyApp")
      .config("spark.mongodb.output.uri", "mongodb://127.0.0.1/test.user")
      .getOrCreate()

    // 设置log级别
    spark.sparkContext.setLogLevel("WARN")

    val document1 = new Document()
    document1.append("name", "sunshangxiang").append("age", 18).append("sex", "female")
    val document2 = new Document()
    document2.append("name", "diaochan").append("age", 24).append("sex", "female")
    val document3 = new Document()
    document3.append("name", "huangyueying").append("age", 23).append("sex", "female")

    val seq = Seq(document1, document2, document3)
    val df = spark.sparkContext.parallelize(seq)

    // 将数据写入mongo
    MongoSpark.save(df)

    spark.stop()
    System.exit(0)
  }
}

```

