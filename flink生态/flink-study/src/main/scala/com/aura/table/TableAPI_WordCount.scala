package com.aura.table

import com.aura.sink.MySink
import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api.scala._
import org.apache.flink.table.api.{Table, TableEnvironment}

/**
  * 作者： 马中华   https://blog.csdn.net/zhongqi2513
  * 时间： 2019/3/16 15:20
  * 描述： 
  */
object TableAPI_WordCount {

  def main(args: Array[String]): Unit = {

    // 测试数据
    val data = Seq("hello", "hadoop", "hello", "spark", "hello", "storm", "hello", "flink")

    // Stream运行环境
    val streamEnv = StreamExecutionEnvironment.getExecutionEnvironment
    val tableEnv = TableEnvironment.getTableEnvironment(streamEnv)

    // 最简单的获取Source方式
    val source:Table = streamEnv.fromCollection(data).toTable(tableEnv, 'word)

    // 单词统计核心逻辑
    val result:Table = source.groupBy('word)              // 单词分组
      .select('word, 'word.count)                   // 单词统计


    // 自定义Sink
    val sink = new MySink
    // 计算结果写入sink
    result.toRetractStream[(String, Long)].addSink(sink)

    streamEnv.execute()
  }
}
