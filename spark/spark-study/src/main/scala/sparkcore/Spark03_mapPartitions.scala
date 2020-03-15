package sparkcore

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD

object Spark03_mapPartitions {
  def main(args: Array[String]): Unit = {
    var config: SparkConf = new SparkConf().setMaster("local[*]").setAppName("WordCount")

    //创建Spark上下文对象
    var sc: SparkContext = new SparkContext(config)
    //map算子
    var listRDD: RDD[Int] = sc.makeRDD(1 to 10) //这里的to 是包含  10的， unto 是不包含10 的
    /**
      * mapPartition 是一次发送一个分区过去计算
      * map 是一次只发送一个数据
      * 因此在 IO传输上,mapPartition效率高些
      * 但是 mapPartition 会发生 OOM
      */
    var mapPartitions: RDD[Int] = listRDD.mapPartitions(datas => {
      //这里的datas是一个分区里面的数据,很多个.datas 是个集合.
      datas.map(datas =>datas*2)//这里是 scala计算，不是RDD计算，会把这个整个发送给执行器exceuter
    })

    mapPartitions.collect().foreach(println)

  }

}
