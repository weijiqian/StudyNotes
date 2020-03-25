package sparkStreaming

import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Tom
  *
  */
object SparkStreaming02_filedata {
  def main(args: Array[String]): Unit = {
    val conf:SparkConf = new SparkConf().setMaster("local[*]").setAppName("streaming")
    val sparkContext = new SparkContext(conf)
    val streamingContext = new StreamingContext(sparkContext,Seconds(3))

    /**
      * 1）文件需要有相同的数据格式；
      * 2）文件进入 dataDirectory的方式需要通过移动或者重命名来实现；
      * 3）一旦文件移动进目录，则不能再修改，即便修改了也不会读取新数据；
      *
      * Spark Streaming 将会监控 dataDirectory 目录并不断处理移动进来的文件，记住目前不支持嵌套目录
      * 没人用这个 用flume 或者kafka
      */
    val fileStream: DStream[String] = streamingContext.textFileStream("in")
    val flatStream: DStream[String] = fileStream.flatMap(line => line.split(" "))
    val mapStream: DStream[(String, Int)] = flatStream.map(word => (word,1))
    val resultStream: DStream[(String, Int)] = mapStream.reduceByKey(_ + _)
    resultStream.print()
    //开始运行
    streamingContext.start()

    //等待
    streamingContext.awaitTermination()

  }
}
