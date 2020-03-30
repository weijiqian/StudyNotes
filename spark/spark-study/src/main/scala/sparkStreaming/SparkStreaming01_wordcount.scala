package sparkStreaming

import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * @Auther Tom
  * @Date 2020-03-18 10:05
  */

object SparkStreaming01_wordcount {
  def main(args: Array[String]): Unit = {
    val conf:SparkConf = new SparkConf().setMaster("local[*]").setAppName("streaming")
    val sparkContext = new SparkContext(conf)
    val streamingContext = new StreamingContext(sparkContext,Seconds(3))

    //从指定的端口中采集数据
    //nc -lc 8888   hadoop1  下往8888端口发数据。
    val dstream: ReceiverInputDStream[String] = streamingContext.socketTextStream("hadoop1",8888)
    val wordStream: DStream[String] = dstream.flatMap(word => word.split(" "))
    val mapStream: DStream[(String, Int)] = wordStream.map((_,1))
    val reduceDS: DStream[(String, Int)] = mapStream.reduceByKey(_ + _)
    reduceDS.print()

    //开始运行
    streamingContext.start()

    //等待
    streamingContext.awaitTermination()


  }
}


