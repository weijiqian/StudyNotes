package com.aura.table

import com.aura.source.Student
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api._
import org.apache.flink.table.api.scala._
import org.apache.flink.table.sources.CsvTableSource

/**
  * 请统计每个部门多少人
  *
  * 95008,李娜,女,18,CS
  * 95021,周二,男,17,MA
  * 95022,郑明,男,20,MA
  *
  *
  * student
  * id,name,sex,age,department
  *
  *
  * flink的table api有两种操作方式：
  *
  * 1、DSL
  *
  *   table.groupBy(...).select(...).orderBy()
  *
  *     关于传参：
  *       1、字符串
  *       2、Expression表达式
  *
  * 2、SQL风格
  *
  *   val table:Table = tableEnv.sqlQuery(sql)
  *
  *
  *
  *   关于如何创建一个表对象有三种方式：
  *
  *   1、fromDataSet
  *
  *   2、regisgterDataSet
  *
  *   3、registerTableSource
  */
object TableAPI_001 {

  def main(args: Array[String]): Unit = {


    val datasetENV = ExecutionEnvironment.getExecutionEnvironment
    val tableEnv = TableEnvironment.getTableEnvironment(datasetENV)


    val dataStreamENV = StreamExecutionEnvironment.getExecutionEnvironment
    val tableEnv2 = TableEnvironment.getTableEnvironment(dataStreamENV)


    val hdfsDataPath = "hdfs://myha01/flinktest/input/student.csv"
    val dataset1: DataSet[Student] = datasetENV.readCsvFile[Student](hdfsDataPath,
      includedFields = Array[Int](0,1,2,3,4),
      pojoFields = Array[String]("id","name","sex","age","department")
    )

    /**
      * 第三种加载得到Table对象的方式
      */
    val studentCsvScource: CsvTableSource = new CsvTableSource(hdfsDataPath,
      Array[String]("id", "name", "sex", "age", "department"),
      Array[TypeInformation[_]](Types.INT, Types.STRING, Types.STRING, Types.INT, Types.STRING)
      ,fieldDelim = ","
      ,ignoreFirstLine = false
    )
    tableEnv.registerTableSource("studentTable1", studentCsvScource)

    val resultTable123: Table = tableEnv.scan("studentTable1")
    val resultDataSet123: DataSet[Student] = tableEnv.toDataSet[Student](resultTable123)
    resultDataSet123.print()
    println("----------------------------------------")



    tableEnv.registerDataSet("studentTable", dataset1)
    val allDataTable: Table = tableEnv.scan("studentTable")
    allDataTable.printSchema()


    // 传入对应的字符串
    /*val resultTable11: Table = allDataTable.groupBy("department")
      .select("department, count(id) as total")*/

    // Expresssion语法
    val resultTable11: Table = allDataTable.groupBy("department")
      .select('department, 'age.max)


    tableEnv.toDataSet[(String, Int)](resultTable11).print()



    val resultTable: Table = tableEnv.sqlQuery("select id,name,sex from studentTable where age >= 20")
    resultTable.printSchema()
    val lastResultDataSet: DataSet[(Int, String, String)] = tableEnv.toDataSet[(Int, String, String)](resultTable)
    lastResultDataSet.print()

    //    datasetENV.execute()
  }
}
