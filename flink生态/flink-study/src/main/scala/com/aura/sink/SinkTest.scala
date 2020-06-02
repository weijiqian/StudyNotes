package com.aura.sink

import org.apache.flink.api.scala._
import org.apache.flink.core.fs.FileSystem.WriteMode
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment

object SinkTest {

  def main(args: Array[String]): Unit = {


    val env = ExecutionEnvironment.getExecutionEnvironment

    val streamEnv = StreamExecutionEnvironment.getExecutionEnvironment

    val dataSet: DataSet[String] = env.fromElements("101,huangbo,111", "102,xuzheng,222","103,wangbaoqiang,333")


    val resultDataSet: DataSet[(String, String, String)] = dataSet.map(x => {
      val splits = x.split(",")
      (splits(0), splits(1), splits(2))
    })


    /**
      * 第一种方式； 直接打印输出
      */
//    resultDataSet.print()
//    resultDataSet.printToErr()
//    resultDataSet.collect()


    /**
      * 第二种方式：输出到文件
      */
    val localDataPath:String = "c:/flinktest/output5"
//    resultDataSet.writeAsText(localDataPath, WriteMode.OVERWRITE)
    resultDataSet.writeAsCsv(localDataPath,
      rowDelimiter = "\n",
      fieldDelimiter = "--",
      WriteMode.OVERWRITE
    )


    /**
      * 第三种方式： 自定义
      */



    env.execute("jobname")
  }
}
