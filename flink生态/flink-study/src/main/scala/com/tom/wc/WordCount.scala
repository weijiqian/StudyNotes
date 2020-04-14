package com.tom.wc

import org.apache.flink.api.java.ExecutionEnvironment

/**
  * @Auther Tom
  * @Date 2020-04-11 20:40
  * @描述 离线单次统计
  */
object WordCount {
  def main(args: Array[String]): Unit = {
    // 创建一个批处理的执行环境
    val env = ExecutionEnvironment.getExecutionEnvironment

    // 从文件中读取数据
    val inputPath = "in/hello.txt"
    val inputDataSet = env.readTextFile(inputPath)

    implicit
    // 分词之后做count
    val wordCountDataSet = inputDataSet.flatMap(data => data.split(" "))
      .map( (_, 1) )
      .groupBy(0)
      .sum(1)

    // 打印输出
    wordCountDataSet.print()
  }
}
