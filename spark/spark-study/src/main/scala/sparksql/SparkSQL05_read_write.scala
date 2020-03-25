package sparksql

import java.util.Properties

import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, SparkSession}

/**
  * @Auther Tom
  * @Date 2020-03-18 08:54
  */

object SparkSQL05_read_write {
  def main(args: Array[String]): Unit = {
    val conf :SparkConf = new SparkConf().setMaster("local[*]").setAppName("read_write")
    val sparkSession: SparkSession = SparkSession.builder().config(conf).getOrCreate()


    /**
      * 1 .Parquet
      */
    val parquetDF: DataFrame = sparkSession.read.parquet("in/data.parquet")
    parquetDF.write.parquet("in/data.parquet")

    /**
      * json
      */
    val jsonDF: DataFrame = sparkSession.read.json("in/data.json")
    jsonDF.write.json("out/data.json")

    /**
      * jdbc
      */
      //方法1
    val jdbcDF = sparkSession
      .read.
      format("jdbc")
      .option("url", "jdbc:mysql://master01:3306/rdd")
      .option("dbtable", "rddtable")
      .option("user", "root")
      .option("password", "hive")
      .load()

    //方法2
    val connectionProperties = new Properties()
    connectionProperties.put("user", "root")
    connectionProperties.put("password", "hive")
    val jdbcDF2 = sparkSession.read
      .jdbc("jdbc:mysql://master01:3306/rdd", "rddtable", connectionProperties)

    //方法1
    jdbcDF.write
      .format("jdbc")
      .option("url", "jdbc:mysql://master01:3306/rdd")
      .option("dbtable", "rddtable2")
      .option("user", "root")
      .option("password", "hive")
      .save()

    //方法2
    jdbcDF2.write
      .jdbc("jdbc:mysql://master01:3306/mysql", "db", connectionProperties)

    // Specifying create table column data types on write
    jdbcDF.write
      .option("createTableColumnTypes", "name CHAR(64), comments VARCHAR(1024)")
      .jdbc("jdbc:mysql://master01:3306/mysql", "db", connectionProperties)


    sparkSession.stop()
  }


}
