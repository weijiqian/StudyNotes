package sparkStreaming

import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}

/**
  * @Auther Tom
  * @Date 2020-03-18 14:08
  *     有状态转换
  */

object SparkStreaming05_UpdateState {
  def main(args: Array[String]): Unit = {
    val conf:SparkConf = new SparkConf().setMaster("local[*]").setAppName("myrecevier")
    val sparkContext = new SparkContext(conf)
    val context: StreamingContext = new StreamingContext(sparkContext,Seconds(3))

    context.checkpoint("in")
    //kafka采集数据
    //第二个 String 才是数据
    var kafkaDStream: ReceiverInputDStream[(String, String)] = KafkaUtils.createStream(context, "linux1:2181", "xiaodong", Map("xiaodong" -> 3))

    val resultDS: DStream[(String, Int)] = kafkaDStream.flatMap(t=>t._2.split(" ")).map((_,1)).reduceByKey(_ + _)

    //将转换后的数据聚合在一起处理
    /**
      * 无状态 :  只计算当前批次的数据
      * 有状态 :  统计所有数据
      */
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
