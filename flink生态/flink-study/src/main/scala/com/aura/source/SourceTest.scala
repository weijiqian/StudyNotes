package com.aura.source

import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.types.StringValue

object SourceTest {

  def main(args: Array[String]): Unit = {

    val env = ExecutionEnvironment.getExecutionEnvironment
    val streamEnv = StreamExecutionEnvironment.getExecutionEnvironment

    // 第一种方式： 从本地集合
    val dataSource1: DataSet[String] = env.fromElements[String]("hello a", "hi b", "a b c")
    val dataSource2: DataSet[Int] = env.fromCollection(Seq(1,2,3,4,5,5))
    val dataSource3: DataSet[Int] = env.fromCollection(List(1,2,3,4,5,6))
    val dataSource4: DataSet[(String, Int)] = env.fromCollection(Map("a" -> 1, "b" -> 2))
    val dataResultSet: DataSet[Long] = env.generateSequence(1, 100)


    /**
      * 第二种方式： 从文件
      */
    val localDataPath :String = "file:///c:/flinktest/input/"
    val hdfsDataPath: String = "hdfs://myha01/flinktest/input/"

    val dataSource5: DataSet[String] = env.readTextFile(localDataPath, "UTF-8")
    val dataSource6: DataSet[Int] = env.readFileOfPrimitives[Int](localDataPath)
    val dataSource7: DataSet[StringValue] = env.readTextFileWithValue(localDataPath)

    val dataSource_csv: DataSet[Student] = env.readCsvFile[Student](hdfsDataPath,
      lineDelimiter = "\n",
      fieldDelimiter = ",",
      quoteCharacter = null,
      ignoreFirstLine = false,
      ignoreComments = null,
      lenient = false,
      includedFields = Array[Int](0, 1, 2, 3, 4),
      pojoFields = Array[String]("id", "name", "sex", "age", "department")
    )
    dataSource_csv.print()


    /**
      * 第三种方式： 从网络端口
      */
    streamEnv.socketTextStream("hadoop02", 7896, delimiter = '\n')


    /**
      * 第四种： 自定义
      *   自己编写一个类，去继承： RichSourceFunction
      */



  }
}


case class Student(id:Int, name:String, sex:String, age:Int, department:String)
