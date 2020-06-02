package com.aura.source

import org.apache.flink.streaming.api.scala._

object MySourceTest {

  def main(args: Array[String]): Unit = {


    val env:StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment


    val dataStream: DataStream[Student] = env.addSource(new MySource)



    dataStream.print()


    env.execute()

  }
}
