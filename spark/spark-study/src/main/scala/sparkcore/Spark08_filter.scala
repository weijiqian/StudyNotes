package sparkcore

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD

/**
  * @Auther Tom
  * @Date 2020-03-15 23:53
  */

object Spark08_filter {
  def main(args: Array[String]): Unit = {
    var config: SparkConf = new SparkConf().setMaster("local[*]").setAppName("WordCount")

    //创建Spark上下文对象

    var sc: SparkContext = new SparkContext(config)

    var listRDD: RDD[Int] = sc.makeRDD( 1 to 16)
    //过滤，满足条件则取出来
    var filterRDD: RDD[Int] = listRDD.filter(x => x % 2 == 0)
    filterRDD.collect().foreach(println)
  sc.stop()
  }
}
