package sparkcore

import org.apache.spark.{SparkConf, SparkContext}

import scala.util.parsing.json.JSON

/**
  * @Auther Tom
  * @Date 2020-03-16 20:47
  */

object Spark25_JSON {
  def main(args: Array[String]): Unit = {

    val config :SparkConf = new SparkConf().setMaster("local[*]").setAppName("WordCount")

    //创建spark上下文对象
    val sc = new SparkContext(config)

    /**
      * {"name":"123", "age":20}
      * {"name":"456", "age":20}
      * {"name":"789", "age":20}
      */
    val json = sc.textFile("in/user.json")

    //这里是重点
    val result  = json.map(JSON.parseFull)

    result.foreach(println)
    //释放资源
    sc.stop()
  }


}

