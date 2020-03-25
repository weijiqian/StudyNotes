package sparkcore

import org.apache.commons.lang.mutable.Mutable
import org.apache.spark.{Partitioner, SparkConf, SparkContext}
import org.apache.spark.rdd.RDD

import scala.collection.mutable.ArrayBuffer

/**
  * @Auther Tom
  * @Date 2020-03-16 11:32
  */

object Spark13_MyPartitioner {

  def main(args: Array[String]): Unit = {
    var config: SparkConf = new SparkConf().setMaster("local[*]").setAppName("WordCount")
    //创建Spark上下文对象
    var sc: SparkContext = new SparkContext(config)
    //从指定
    val listRDD: RDD[(Int, String)] = sc.makeRDD(List((1,"a"),(2,"b"),(3,"c"),(4,"d"),(5,"e")))
    var partRDD: RDD[(Int, String)] = listRDD.partitionBy(new MyPartitioner(2))
    //    var partRDD: RDD[(String, Int)] = listRDD.partitionBy(new org.apache.spark.HashPartitioner(2))
    val glomRDD: RDD[Array[(Int, String)]] = partRDD.glom()
//    glomRDD.collect().foreach(list => printf(list.mkString(",")))


//    val value: RDD[((Int, String), (Int, String))] = glomRDD.mapPartitionsWithIndex {
//      case (num, datas) => {
//        datas.map(t => (t(0), t(1)))
//      }
//    }
//    value
//    value.collect().foreach{
//      case ((id,name),name2)=>{
//        printf(id + " == " + name + "==" + name2)
//      }
//    }

   sc.stop()
  }

  //声明分区器
  class MyPartitioner(partitions:Int) extends Partitioner {

    /**
      * 返回的分区数
      * @return
      */
    override def numPartitions: Int = {
      partitions
    }

    /**
      * 给定键的分区编号
      * 分区的规则
      * @param key
      * @return
      */
    override def getPartition(key: Any): Int = {
      1
    }
  }
}


