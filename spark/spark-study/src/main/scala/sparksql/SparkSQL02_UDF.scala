package sparksql

import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, SparkSession}

/**
  * @Auther Tom
  * @Date 2020-03-28 10:40
  * @描述 SparkSQL中的UDF相当于是1进1出
  */
object SparkSQL02_UDF {
  def main(args: Array[String]): Unit = {
    val conf :SparkConf = new SparkConf().setMaster("local[*]").setAppName("demo")
    val sparkSession: SparkSession = SparkSession.builder().config(conf).getOrCreate()

    val rdd: RDD[(String, Int)] = sparkSession.sparkContext.makeRDD(List(("tom",20),("jack",18),("tony",30)))

    //toDF
    import sparkSession.implicits._

    //函数的意思:给每个人的年龄+num
    sparkSession.udf.register("addAge",(age:Int,num:Int) => age + num)

    val df: DataFrame = rdd.toDF("name","age")
    df.createTempView("user")

    val dfResult: DataFrame = sparkSession.sql("select addAge(age,10) from user")
    dfResult.show()

    sparkSession.stop()
  }
}
