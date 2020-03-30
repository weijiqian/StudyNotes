package sparksql

import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.expressions.{MutableAggregationBuffer, UserDefinedAggregateFunction}
import org.apache.spark.sql.types.{DataType, DoubleType, LongType, StructType}
import org.apache.spark.sql.{DataFrame, Row, SparkSession}

/**
  * @Auther Tom
  * @Date 2020-03-17 17:04
  *  自定义函数 -- 入门版  用户自定义聚合函数。
  */

object SparkSQL03_UDAF {

  def main(args: Array[String]): Unit = {
    val conf :SparkConf = new SparkConf().setMaster("local[*]").setAppName("demo")
    val sparkSession: SparkSession = SparkSession.builder().config(conf).getOrCreate()

    val rdd: RDD[(String, Int)] = sparkSession.sparkContext.makeRDD(List(("tom",20),("jack",18),("tony",30)))

    //toDF
    import sparkSession.implicits._

    val ageAvgFunction = new MyAgeAvgFunction
    sparkSession.udf.register("avgAge",ageAvgFunction)

    val df: DataFrame = rdd.toDF("name","age")
    df.createTempView("user")

    val dfResult: DataFrame = sparkSession.sql("select avgAge(age) from user")
    dfResult.show()

    sparkSession.stop()
  }
  
}

/**
  * 求平均年龄
  */
class MyAgeAvgFunction extends UserDefinedAggregateFunction{

  //函数输入的数据结构
  override def inputSchema: StructType = {
    new StructType().add("age",LongType)
  }

  //计算时的数据结构
  override def bufferSchema: StructType = {
    new StructType().add("sum",LongType).add("count",LongType)
  }
  //函数返回的数据类型
  override def dataType: DataType = DoubleType

  //函数是否稳定: 给相同的值，在不同的时间，结果是否一致
  override def deterministic: Boolean = true

  //计算前缓存区的初始化
  override def initialize(buffer: MutableAggregationBuffer): Unit = {
    //没有名称，只有结构，只能通过标记位来确定
    buffer(0) = 0l
    buffer(1) = 0l
  }


  /**
    * //根据查询结果  更新数据
    * @param buffer  计算器里面保存的变量
    * @param input   输入的数据
    */
  override def update(buffer: MutableAggregationBuffer, input: Row): Unit = {
    buffer(0) = buffer.getLong(0) + input.getLong(1)//统计和
    buffer(1) = buffer.getLong(1) + 1 //统计个数
  }

  //合并各个分区数据
  override def merge(buffer1: MutableAggregationBuffer, buffer2: Row): Unit ={
    buffer1(0) = buffer1.getLong(0) + buffer2.getLong(0)
    buffer1(1) = buffer1.getLong(1) + buffer2.getLong(1)

  }

  //计算逻辑。。。
  override def evaluate(buffer: Row): Any = {
    buffer.getLong(0).toDouble / buffer.getLong(1).toDouble
  }
}