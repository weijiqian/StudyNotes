package sparksql

import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql._
import org.apache.spark.sql.expressions.Aggregator

/**
  * @Auther Tom
  * @Date 2020-03-17 19:39
  * 自定义函数--高级版
  */

object SparkSQL04_UDAF_Class {
  def main(args: Array[String]): Unit = {
    val conf :SparkConf = new SparkConf().setMaster("local[*]").setAppName("demo")
    val sparkSession: SparkSession = SparkSession.builder().config(conf).getOrCreate()

    val rdd: RDD[(String, Int)] = sparkSession.sparkContext.makeRDD(List(("tom",20),("jack",18),("tony",30)))

    //toDF
    import sparkSession.implicits._

    val udf = new MyAgeAvgClassFunction
    //2. 将聚合函数转换为查询列，因为传入的是对象
    val avgCol: TypedColumn[UserBean, Double] = udf.toColumn.name("avg")

    val userRDD: RDD[UserBean] = rdd.map {
      case (name, age) => {
        UserBean(name, age)
      }
    }

    val ds: Dataset[UserBean] = userRDD.toDS()
    //应用函数，因为传入的是对象，并且每条进行处理
    ds.select(avgCol).show()
    sparkSession.stop()
  }

}


case class UserBean(name: String, age: BigInt)
//这里输入 数据类型 需要改为BigInt，不能为Int。因为程序读取文件的时候，不能判断int类型到底多大，所以会报错 truncate

case class AvgBuffer(var sum: BigInt, var count: Int)

/**
  * 求平均年龄
  */
class MyAgeAvgClassFunction extends Aggregator[UserBean, AvgBuffer, Double]{

  //初始化
  override def zero: AvgBuffer = {
    AvgBuffer(0,0)
  }

  //计算过程
  override def reduce(b: AvgBuffer, a: UserBean): AvgBuffer = {
    b.sum = b.sum + a.age
    b.count = b.count + 1
    b
  }

  //合并分区数据
  override def merge(b1: AvgBuffer, b2: AvgBuffer): AvgBuffer = {
    b1.count = b1.count + b2.count
    b1.sum = b1.sum + b2.sum
    b1
  }

  override def finish(reduction: AvgBuffer): Double = {
    reduction.sum.toDouble / reduction.count.toDouble
  }

  //数据类型转码，自定义类型 基本都是Encoders.product
  override def bufferEncoder: Encoder[AvgBuffer] = Encoders.product

  //基本数据类型：Encoders.scala。。。
  override def outputEncoder: Encoder[Double] = Encoders.scalaDouble
}
