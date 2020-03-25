package sparkStreaming

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.kafka.KafkaUtils

/**
  * @Auther Tom
  * @Date 2020-03-18 14:31
  *  窗口操作
  */
object SparkStreaming06_window {
  def main(args: Array[String]): Unit = {
    val conf:SparkConf = new SparkConf().setMaster("local[*]").setAppName("myrecevier")
    val sparkContext = new SparkContext(conf)
    val context: StreamingContext = new StreamingContext(sparkContext,Seconds(3))

    //kafka采集数据
    //第二个 String 才是数据
    var kafkaDStream: ReceiverInputDStream[(String, String)] = KafkaUtils.createStream(context, "linux1:2181", "xiaodong", Map("xiaodong" -> 3))

    /**
      * 窗口大小应该为采集周期的整数倍，窗口滑动步长也应该是采集周期的整数倍
      * 采集周期  第17行  3秒
      * 第一个参数  窗口大小  9秒
      * 第二个参数   滑动大小  3秒
      */
    var windowDStream: DStream[(String, String)] = kafkaDStream.window(Seconds(9), Seconds(3))

    val resultDS: DStream[(String, Int)] = windowDStream.flatMap(t=>t._2.split(" ")).map((_,1)).reduceByKey(_ + _)

    //将转换后的数据聚合在一起处理
    var stateDStream: DStream[(String, Int)] = resultDS.updateStateByKey {
      case (seq, buffer) => {
        var sum = buffer.getOrElse(0) + seq.sum
        Option(sum)
      }
    }

    stateDStream.print()
    context.start()
    context.awaitTermination()



  }
}
