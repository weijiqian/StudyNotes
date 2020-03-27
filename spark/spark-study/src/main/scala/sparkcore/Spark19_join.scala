package sparkcore

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD

/**
 * @Author: Tom
 * @Date:  2020-03-25  20:00
 * @Description: join操作  (K,V)和(K,W)   ===> (K,(V,W))
  *              只返回都有的key 以及值  ,
  *              leftOuterJoin  保留左边的所有数据,右边没有对应值的为空.
**/
object Spark19_join {
  def main(args: Array[String]): Unit = {
    val config: SparkConf = new SparkConf().setMaster("local[*]").setAppName("WordCount")
    //创建Spark上下文对象
    val sc: SparkContext = new SparkContext(config)

    val listRDD: RDD[(String, Int)] = sc.makeRDD(List(("a", 1), ("b", 2), ("c", 3), ("d", 4),("x",10)))
    val listRDD2: RDD[(String, String)] = sc.makeRDD(List(("a", "a"), ("b", "b"), ("c", "c"),("y","y")))

    val joinRDD: RDD[(String, (Int, String))] = listRDD.join(listRDD2)
    joinRDD.collect().foreach{
      case (a,(b,c)) =>{
        println("(" + a + ",(" + b + "," + c + "))")
      }
    }
    // 返回结果   x 和 y 都删了
    // (a,(1,a))
    // (b,(2,b))
    // (c,(3,c))


    println("=============leftOuterJoinRDD================")
    // Option[String]  表示可能不存在
    val leftOuterJoinRDD: RDD[(String, (Int, Option[String]))] = listRDD.leftOuterJoin(listRDD2)
    leftOuterJoinRDD.collect().foreach{
      case (a,(b,c)) =>{
        println("(" + a + ",(" + b + "," + c + "))")
      }
    }

    /** 结果
      * (d,(4,None))
      * (x,(10,None))
      * (a,(1,Some(a)))
      * (b,(2,Some(b)))
      * (c,(3,Some(c)))
      */
    sc.stop()

  }
}
