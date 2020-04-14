package com.tom.wc

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment

/**
  * @Auther Tom
  * @Date 2020-04-12 16:37
  * @描述 TODO
  */
object flink02_wordcount {
  // 1. 创建执行环境
  val env = StreamExecutionEnvironment.getExecutionEnvironment
//  env.setParallelism(1)
//  env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
  val dataStream = env.readTextFile("D:\\Projects\\BigData\\UserBehaviorAnalysis\\HotItemsAnalysis\\src\\main\\resources\\UserBehavior.csv")

}
