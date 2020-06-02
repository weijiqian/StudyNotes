package com.aura

import org.apache.flink.api.scala._

/**
  * 离线计算程序：
  *
  *   DataSet   ExecutionEnvironment
  *   DataStream  StreamExecutionEnvironment
  */
object DataSet_HelloWorld {

  def main(args: Array[String]): Unit = {


    val dataSetEnv: ExecutionEnvironment = ExecutionEnvironment.getExecutionEnvironment


    val dataset: DataSet[String] = dataSetEnv.fromElements("a b","c a","a b")


    case class WordCount(word:String, count:Long)
    val dataset2: DataSet[(String, Int)] = dataset.flatMap(x => x.split(" ")).map((_, 1))




    /**
      * datastream: keyBy
      * dataset: groupBy
      *
      * dataset2.groupBy(0).sum(1) = dataset2.reduceByKey(_+_)
      *
      */
    val dataset3: GroupedDataSet[(String, Int)] = dataset2.groupBy(0)
    val dataset4: AggregateDataSet[(String, Int)] = dataset3.sum(1)



    dataset4.print()

  }
}
