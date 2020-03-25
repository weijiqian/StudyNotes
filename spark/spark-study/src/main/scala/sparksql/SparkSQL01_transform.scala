package sparksql

import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}

/**
  * @Auther Tom
  * @Date 2020-03-17 17:04
  */

object SparkSQL01_transform {

  def main(args: Array[String]): Unit = {
    val conf :SparkConf = new SparkConf().setMaster("local[*]").setAppName("demo")
    val sparkSession: SparkSession = SparkSession.builder().config(conf).getOrCreate()

    val rdd: RDD[(String, Int)] = sparkSession.sparkContext.makeRDD(List(("tom",20),("jack",18),("tony",30)))

    //toDF
    import sparkSession.implicits._
    val frame: DataFrame = rdd.toDF("name","age")

    //toDS
    val ds: Dataset[User] = frame.as[User]

    //toDF
    val df: DataFrame = ds.toDF()

    //tordd
    val rdd1: RDD[Row] = df.rdd

    //rdd -> ds
    val userRDD: RDD[User] = rdd.map {
      case (name, age) => {
        User(name, age)
      }
    }
    val ds2: Dataset[User] = userRDD.toDS()
    val rdd3: RDD[User] = ds2.rdd



    //打印
    rdd1.foreach(row => {
      //索引 从0开始
      printf(row.getString(0))
      printf(row.getInt(1).toString)
    })

    sparkSession.stop()
  }
  
}
case class User(name:String,age:Int)