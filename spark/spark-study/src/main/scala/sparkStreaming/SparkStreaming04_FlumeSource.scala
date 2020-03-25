package sparkStreaming

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.streaming.flume.{FlumeUtils, SparkFlumeEvent}


/**
  * @Auther Tom
  * @Date 2020-03-18 14:08
  *      flume采集器
  */

object SparkStreaming04_FlumeSource {
  def main(args: Array[String]): Unit = {
    val conf:SparkConf = new SparkConf().setMaster("local[*]").setAppName("myrecevier")
    val sparkContext = new SparkContext(conf)
    val context: StreamingContext = new StreamingContext(sparkContext,Seconds(3))

    /**
      * flume  配置
      * a1.sinks = spark
      * a1.sinks.spark.type = org.apache.spark.streaming.flume.sink.SparkSink
      * a1.sinks.spark.hostname = hadoop1
      * a1.sinks.spark.port = 9999
      * a1.sinks.spark.channel = memoryChannel
      */

    val flumeDS: ReceiverInputDStream[SparkFlumeEvent] = FlumeUtils.createStream(context,"hadoop1",9999,StorageLevel.MEMORY_AND_DISK_2)

    val da: DStream[String] = flumeDS.map(data => {
      new String(data.event.getBody.array())
    })
    val resultDS: DStream[(String, Int)] = da.flatMap(t=>t.split(" ")).map((_,1)).reduceByKey(_ + _)

    resultDS.print()

    context.start()
    context.awaitTermination()




  }
}
