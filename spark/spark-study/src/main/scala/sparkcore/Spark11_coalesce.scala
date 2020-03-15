package sparkcore

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD

/**
  * @Auther Tom
  * @Date 2020-03-16 00:01
  */

object Spark11_coalesce {
  def main(args: Array[String]): Unit = {
    var config: SparkConf = new SparkConf().setMaster("local[*]").setAppName("WordCount")
    var sc: SparkContext = new SparkContext(config)
    var listRDD: RDD[Int] = sc.makeRDD( 1 to 16)
    println("缩减分区前 = " +listRDD.partitions.size)

    /**
      * 缩减分区，可以简单的理解为合并分区，没有shuffle，会把剩余的数据存储在最后
      */
    var coalesceRDD: RDD[Int] = listRDD.coalesce(3)
    println("缩减分区后 = " +coalesceRDD.partitions.size)
    //    coalesceRDD.saveAsTextFile("output")
    sc.stop()
  }
}
