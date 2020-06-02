package com.aura.source

import java.sql.{Connection, DriverManager, ResultSet, Statement}

import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.source.{RichSourceFunction, SourceFunction}

/**
  * 自定义个数据源： 读取MySQL
  *
  * 总共定义了四个方法
  */
class MySource extends RichSourceFunction[Student]{

  private var statement:Statement = _
  private var connection:Connection = _

  /**
    * 在整个soruce对象初始化之后就会执行一次的
    * @param parameters
    */
  override def open(parameters: Configuration): Unit = {

    Class.forName("com.mysql.jdbc.Driver")
    val url = "jdbc:mysql://hadoop02:3306/bigdata"
    connection = DriverManager.getConnection(url, "root", "root")
    statement = connection.createStatement()
  }

  // 不停的执行，然后发送数据到下一个组件
  override def run(sourceContext: SourceFunction.SourceContext[Student]): Unit = {
    val sql = "select id,name,sex,age,department from student"
    val resultSet:ResultSet = statement.executeQuery(sql)

    while(resultSet.next()){
      val id = resultSet.getInt("id")
      val name = resultSet.getString("name")
      val sex = resultSet.getString("sex")
      val age = resultSet.getInt("age")
      val department = resultSet.getString("department")

      sourceContext.collect(Student(id, name, sex, age, department))
    }
  }

  // 取消
  override def cancel(): Unit = {


  }

  /**
    * 关闭
    */
  override def close(): Unit = {
    statement.close()
    connection.close()
  }
}
