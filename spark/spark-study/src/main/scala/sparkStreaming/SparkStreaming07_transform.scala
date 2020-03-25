package sparkStreaming

import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}

/**
  * @Auther Tom
  * @Date 2020-03-18 14:08
  *   transform
  */

object SparkStreaming07_transform {
  def main(args: Array[String]): Unit = {
    val conf:SparkConf = new SparkConf().setMaster("local[*]").setAppName("myrecevier")
    val sparkContext = new SparkContext(conf)
    val context: StreamingContext = new StreamingContext(sparkContext,Seconds(3))

    context.checkpoint("in")
    //kafka采集数据
    //第二个 String 才是数据
    var kafkaDStream: ReceiverInputDStream[(String, String)] = KafkaUtils.createStream(context, "linux1:2181", "xiaodong", Map("xiaodong" -> 3))

    //只执行一次
    kafkaDStream.transform(
      // 代码（Driver)(m  运行采集周期 次)
      rdd => {
        // 代码 （Executer)
        rdd.flatMap(t => t._2.split(" ")).map((_,1)).reduceByKey(_ + _)
      }
    )

    val resultDS: DStream[(String, Int)] = kafkaDStream.flatMap(t=>t._2.split(" ")).map((_,1)).reduceByKey(_ + _)





    context.start()
    context.awaitTermination()




  }

  
  
  
  

}
