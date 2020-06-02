package com.aura.datastream

import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment, _}
import org.apache.flink.streaming.api.windowing.time.Time

object DataStream_WordCount {

  def main(args: Array[String]): Unit = {

    /**
      * flink run -C class xxxx.jar --port 2356 --name myname
      *
      *
      */
      val toolResult: ParameterTool = ParameterTool.fromArgs(args)
      val port:Int = toolResult.getInt("port")
//      val name:String = toolResult.get("name")


    val environment:StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment


    val dataStream: DataStream[String] = environment.socketTextStream("hadoop02", port)


    val resultDS: DataStream[(String, Int)] = dataStream.flatMap(x => x.split(" "))
      .map((_, 1))
      .keyBy(0)
      .timeWindow(Time.seconds(5), Time.seconds(2))
      .sum(1)

    resultDS.print()

    environment.execute("mywordcountstream")
  }
}
