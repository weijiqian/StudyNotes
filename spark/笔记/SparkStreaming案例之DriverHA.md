## Driver HA

- 如果使用的是Client模式就无法实现Driver HA ，我们这里针对的是cluster模式。
- Yarn平台的cluster模式提交任务，AM(AplicationMaster)相当于Driver，如果挂掉会自动启动AM。无需我们手动配置.



**Spark standalone和Mesos资源调度的情况下。实现Driver的高可用有两个步骤:**

第一：提交任务层面，在提交任务的时候加上选项 **- -supervise**,当Driver挂掉的时候会自动重启Driver。

第二：代码层面，使用**JavaStreamingContext.getOrCreate（checkpoint路径，JavaStreamingContextFactory）**

- Driver中元数据包括：
  1. 创建应用程序的配置信息。
  2. DStream的操作逻辑。
  3. job中没有完成的批次数据，也就是job的执行进度。



```scala
/**
  * SparkStreaming案例之DriverHA操作
  * 为了保证DriverHA
  *     1、开启checkpoint机制
  *     2、重写StreamingContext的构建方式
  *     3、修改Spark-submit配置脚本中的两个参数
  *         --deploy-mode   cluster
  *         --supervise
  */
object SparkStreamingDriverHA {
    def main(args: Array[String]): Unit = {
     

        if(args == null || args.length < 4) {
            println(
                """Parameter Errors! Usage: <batchInterval> <hostname> <port> <checkpoint>
                  |batchInterval:   streaming作业启动的间隔频率(s)
                  |hostname     :   监听的网络ip
                  |port         :   监听的网络端口
                  |checkpoint   :   checkpoint
                """.stripMargin)
            System.exit(-1)
        }
        val Array(batchInterval, hostname, port, checkpoint) = args

        def createFunc():StreamingContext = {
            val conf = new SparkConf()
                .setAppName("SparkStreamingNetcat")
            val sc = new SparkContext(conf)
            val ssc = new StreamingContext(sc, Seconds(batchInterval.toLong))

            val input:ReceiverInputDStream[String] = ssc.socketTextStream(hostname, port.toInt, StorageLevel.MEMORY_ONLY)

            val wordDStream:DStream[String] = input.flatMap(line => line.split("\\s+"))

            val retDS:DStream[(String, Int)] = wordDStream.map((_, 1)).reduceByKey(_+_)

            retDS.print()

            //checkpoint
            ssc.checkpoint(checkpoint)
            ssc
        }
				// 这里是重点
        val ssc = StreamingContext.getOrCreate(checkpoint, createFunc)

        ssc.start()
        ssc.awaitTermination()
    }
}

```

