package sparkcore

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD

object Spark05_flatMap {
  def main(args: Array[String]): Unit = {
    var config: SparkConf = new SparkConf().setMaster("local[*]").setAppName("WordCount")

    //创建Spark上下文对象

    var sc: SparkContext = new SparkContext(config)
    //map算子
    var listRDD: RDD[List[Int]] = sc.makeRDD(Array(List(1,2),List(3,4,5,88)))

    //flatMap
    //1,2,3,4
    var flatMapRDD: RDD[Int] = listRDD.flatMap(datas => datas)

    //TODO flatMap  下面怎么写?
//    var flatMapRDD2: RDD[Int] = listRDD.flatMap{
//      datas => datas.toList.map(_,_)
//    }

    flatMapRDD.collect().foreach(println)
  }
  }

