package sparkStreaming

import java.io.{BufferedReader, InputStreamReader}
import java.net.Socket

import org.apache.spark.sql.catalyst.expressions.Second
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.receiver.Receiver
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}


/**
  * @Auther Tom
  * @Date 2020-03-18 11:11
  *
  *  自定义数据源
  */

object SparkStreaming03_MyReceiver {
  def main(args: Array[String]): Unit = {
    val conf:SparkConf = new SparkConf().setMaster("local[*]").setAppName("myrecevier")
    val sparkContext = new SparkContext(conf)
    val context: StreamingContext = new StreamingContext(sparkContext,Seconds(3))

    val ds: ReceiverInputDStream[String] = context.receiverStream(new MyReceiver("hadoop1",8888))

    val resultDS: DStream[(String, Int)] = ds.flatMap(line => line.split(" ")).map((_,1)).reduceByKey(_ + _)
    resultDS.print()

    context.start()
    context.awaitTermination()

  }
}

/**
  * 自定义数据源
  * @param host
  * @param port
  */
class MyReceiver(host: String, port: Int) extends Receiver[String](StorageLevel.MEMORY_ONLY){

  var socket:Socket = null

  override def onStart(): Unit = {
    new Thread(new Runnable {
      override def run(): Unit = {
        receive()
      }
    }).start()
  }

  override def onStop(): Unit = {
    if (socket != null){
      socket.close()
      socket = null
    }
  }

  def receive() = {
    socket = new Socket(host,port)

    var reader: BufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"))
    var line:String = null
    while ((line = reader.readLine())!= None){
      //接受数据以END结尾
      if (!line.equals("END")){
        //保存数据
        this.store(line)
      }
    }
  }

}
