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
      * updateStateByKey  把这一批次key为a的值value和以前的同一个key为a的value聚合在一起
      */
    var stateDStream: DStream[(String, Int)] = resultDS.updateStateByKey {
      //在这个方法里面,不涉及到key,都是value的转换
      //因为都是针对同一个key的不同value做操作
      //这一个批次里面有N条数据.values就是一个集合.  而原来的key是聚合后的,要么有一个值,要么没有值.
      case (values:Seq[Int], old:Option[Int]) => {
        // 如果说，之前是存在这个状态的，那么就以之前的状态作为起点，进行值的累加
        var sum :Int = 0
        //获取原来的值
        if(old.isDefined) {
          sum = old.get
        }

        // values，代表了一个批次中，这一个key对应的所有的值
        for(value <- values) {
          sum += value
        }

        //只需要返回value就行
        Option(sum)
      }
    }


    stateDStream.print()

    context.start()
    context.awaitTermination()




  }

  
  
  
  

}
