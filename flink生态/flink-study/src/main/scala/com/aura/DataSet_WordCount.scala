package com.aura

import org.apache.flink.api.scala._
import org.apache.flink.core.fs.FileSystem.WriteMode

object DataSet_WordCount {

  def main(args: Array[String]): Unit = {


    val dataSetEnv: ExecutionEnvironment = ExecutionEnvironment.getExecutionEnvironment


    val dataset: DataSet[String] = dataSetEnv.fromElements("a b","c a","a b")


    case class WordCount(word:String, count:Long)

    val dataset2: DataSet[WordCount] = dataset.flatMap(x => x.split(" ")).map(word => WordCount(word, 1))



    /**
      * datastream: keyBy
      * dataset: groupBy
      *
      * dataset2.groupBy(0).sum(1) = dataset2.reduceByKey(_+_)
      *
      * Row
      */
    val dataset3: GroupedDataSet[WordCount] = dataset2.groupBy(0)
    val dataset4: AggregateDataSet[WordCount] = dataset3.sum(1)


        dataset4.print()


    /**
      * 如果并行度是1， 你指定的不是目录，是文件
      * 如果并行度大于1，那么指定的就是目录
      */
    /*  val counter: Long = dataset4.count()
      println(counter)
      dataset4.writeAsText("c:/flinktest/output1",
        WriteMode.OVERWRITE)
        .setParallelism(2)*/



    dataSetEnv.execute("myscalaflinkwordcount")
  }
}
