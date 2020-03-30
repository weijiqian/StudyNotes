package sparkStreaming

import org.apache.spark.streaming.dstream.ReceiverInputDStream
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}

/**
  * @Auther Tom
  * @Date 2020-03-28 10:00
  * @描述 map,transform,foreachRDD 的区别
  */
object Ssc08_map_transform {
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

    kafkaDStream.map{case (key,value) =>
        val strings: Array[String] = value.split(" ")
      val tuples: Array[(String, Int)] = strings.map((_,1))
        tuples.reduce((a1,a2) => (a1._1,a1._2 + a2._2) )
    }

    /**
      * transform  的里面是rdd,都在exceter中进行.
      * map里面是具体的数据
      * 平时用tannsform
      */

    context.start()
    context.awaitTermination()

  }

}
