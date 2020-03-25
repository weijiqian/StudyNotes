package sparkcore

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD

/**
  * @Auther Tom
  * @Date 2020-03-15 22:50
  */

object Spark07_groupBy {
  def main(args: Array[String]): Unit = {
    var config: SparkConf = new SparkConf().setMaster("local[*]").setAppName("WordCount")
    //创建Spark上下文对象
    var sc: SparkContext = new SparkContext(config)
    //分组后的数据，形成对偶元组（k-v),k表示分组的key  , value 表示分组的集合
    var listRDD: RDD[Int] = sc.makeRDD( 1 to 16) //这里的to 是包含  10的， unto 是不包含10 的, 后面的2 是确定分区数

    /**
      * 按条件分组
      * 按照函数的返回值来分组
      */
    var groupbyRDD: RDD[(Int, Iterable[Int])] = listRDD.groupBy(i => i % 2)

    groupbyRDD.collect().foreach(println)
  sc.stop()
  }

}
