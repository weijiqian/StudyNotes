package sparkStreaming

import org.apache.commons.codec.StringDecoder
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * @Auther Tom
  * @Date 2020-03-18 14:08
  *      kafka采集器
  */

object SparkStreaming04_KafkaSource {
  def main(args: Array[String]): Unit = {
    val conf:SparkConf = new SparkConf().setMaster("local[*]").setAppName("myrecevier")
    val sparkContext = new SparkContext(conf)
    val context: StreamingContext = new StreamingContext(sparkContext,Seconds(3))


    //2.定义kafka参数
    val brokers = "hadoop102:9092,hadoop103:9092,hadoop104:9092"
    val topic = "source"
    val consumerGroup = "spark"

    //3.将kafka参数映射为map
    val kafkaParam: Map[String, String] = Map[String, String](
      ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG -> "org.apache.kafka.com.atguigu.common.serialization.StringDeserializer",
      ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG -> "org.apache.kafka.com.atguigu.common.serialization.StringDeserializer",
      ConsumerConfig.GROUP_ID_CONFIG -> consumerGroup,
      ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG -> brokers
    )
    //kafka采集数据


    //4.通过KafkaUtil创建kafkaDSteam
    //第二个 String 才是数据
    val kafkaDStream: ReceiverInputDStream[(String, String)] = KafkaUtils.createStream(context,kafkaParam,Map[String,Int]("sourec"->2),StorageLevel.MEMORY_AND_DISK_2)
    val resultDS: DStream[(String, Int)] = kafkaDStream.flatMap(t=>t._2.split(" ")).map((_,1)).reduceByKey(_ + _)

    resultDS.print()

    context.start()
    context.awaitTermination()




  }
}
