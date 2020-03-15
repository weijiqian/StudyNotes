package sparkcore

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD

/**
  * @Auther Tom
  * @Date 2020-03-15 23:54
  */

object Spark09_sample {
  def main(args: Array[String]): Unit = {
    var config: SparkConf = new SparkConf().setMaster("local[*]").setAppName("WordCount")
    var sc: SparkContext = new SparkContext(config)
    //从指定数据集合中进行抽样处理。
    var listRDD: RDD[Int] = sc.makeRDD( 1 to 16) //这里的to 是包含  10的， unto 是不包含10 的, 后面的2 是确定分区数

    /**
      * true为有放回的抽样，false为无放回的抽样，
      * fraction 抽取的数量
      *     不放回抽样 取值 [0,1]
      *     放回抽样  取值 >0
      * seed用于指定随机数生成器种子。
      */
    //    var SampleRDD: RDD[Int] = listRDD.sample(false, 0.4, 1)//不放回
    var SampleRDD: RDD[Int] = listRDD.sample(true, 4, 1)//放回抽样，可重复

    SampleRDD.collect().foreach(println)
    sc.stop()
  }
}
