package sparkcore

import javax.validation.OverridesAttribute.List
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD

object Spark04_mapPartitionsWithIndex {
  def main(args: Array[String]): Unit = {
    var config: SparkConf = new SparkConf().setMaster("local[*]").setAppName("WordCount")

    //创建Spark上下文对象

    var sc: SparkContext = new SparkContext(config)
    //map算子
    var listRDD: RDD[Int] = sc.makeRDD(1 to 10 ,2) //这里的to 是包含  10的， unto 是不包含10 的, 后面的2 是确定分区数

    //方法 使用大括号，是启用模式匹配的效果,//num 是分区号，
    //    var indexRDD: RDD[Int] = listRDD.mapPartitionsWithIndex {
    //      case (num, datas) => {
    //        datas
    //      }
    //    }


    var tupleRDD: RDD[(Int, String)] = listRDD.mapPartitionsWithIndex {
      case (num, datas) => {
        datas.map((_, "分区号：" + num))
      }
    }

    tupleRDD.collect().foreach(println)
  }

}

