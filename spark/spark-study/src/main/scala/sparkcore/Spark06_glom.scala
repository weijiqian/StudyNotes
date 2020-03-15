package sparkcore

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD

/**
  * @Auther Tom
  * @Date 2020-03-15 22:34
  */

object Spark06_glom {
  def main(args: Array[String]): Unit = {
    var config: SparkConf = new SparkConf().setMaster("local[*]").setAppName("WordCount")

    //创建Spark上下文对象

    var sc: SparkContext = new SparkContext(config)
    //map算子,后面2 是两个分区，一定有两个，最后一个分区会把剩下的数据存完。2）和文件分区不一样，文件分区最少会有两个。
    var listRDD: RDD[Int] = sc.makeRDD( 1 to 16,4) //这里的to 是包含  10的， unto 是不包含10 的, 后面的2 是确定分区数

    //将一个分区的数据，放到一个数组中
    //有4个分区,就变成了4个数组
    var glomRDD: RDD[Array[Int]] = listRDD.glom()

    //    glomRDD.collect().foreach(println)
    glomRDD.collect().foreach(array => {
      println(array.mkString(","))
    })

  }
}
