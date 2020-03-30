package sparkStreaming

import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Minutes, Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}

/**
  * @Auther Tom
  * @Date 2020-03-28 15:41
  * @描述 对一个窗口内的数据进行聚合.
  */
object Ssc10_reduceByKeyAndWindow {
  def main(args: Array[String]): Unit = {
    val conf:SparkConf = new SparkConf().setMaster("local[*]").setAppName("myrecevier")
    val sparkContext = new SparkContext(conf)
    val context: StreamingContext = new StreamingContext(sparkContext,Seconds(3))

    context.checkpoint("in")
    //kafka采集数据
    //第二个 String 才是数据
    var kafkaDStream: ReceiverInputDStream[(String, String)] = KafkaUtils.createStream(context, "linux1:2181", "xiaodong", Map("xiaodong" -> 3))

    val mapDStrean: DStream[(String, Int)] = kafkaDStream.transform { rdd =>
      rdd.map { case (a, b) => (b, 1) }
    }
    //每10秒计算一次,每次计算1小时内的数据.
    val redecuDStream: DStream[(String, Int)] = mapDStrean.reduceByKeyAndWindow((a:Int,b:Int) => (a+b),Minutes(60),Seconds(10))

    context.start()
    context.awaitTermination()
  }
}
