package sparksql

import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, SparkSession}

/**
  * @Auther Tom
  * @Date 2020-03-17 17:04
  */

object SparkSQL01_demo {

  def main(args: Array[String]): Unit = {
    val conf :SparkConf = new SparkConf().setMaster("local[*]").setAppName("demo")
    val sparkSession: SparkSession = SparkSession.builder().config(conf).getOrCreate()

    val frame: DataFrame = sparkSession.read.json("in/user.json")
      frame.show()

    sparkSession.stop()



  }


}
