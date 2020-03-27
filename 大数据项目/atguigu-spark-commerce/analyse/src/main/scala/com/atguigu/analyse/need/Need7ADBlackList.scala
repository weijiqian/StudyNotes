package com.atguigu.analyse.need

import java.sql.Date

import com.atguigu.analyse.utils.{AdBlacklist, AdBlacklistDAO, AdUserClickCount, AdUserClickCountDAO}
import com.atguigu.common.conf.ConfigurationManager
import com.atguigu.common.utils.DateUtils
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}

import scala.collection.mutable.ArrayBuffer


/**
  * @Auther Tom
  * @Date 2020-03-27 21:41
  * @描述 需求七:广告黑名单实时统计
  *    将每天对某个广告点击超过 100 次的用户拉黑
  */
object Need7ADBlackList {



  def main(args: Array[String]): Unit = {
    // 构建Spark上下文
    val sparkConf = new SparkConf().setAppName("streamingRecommendingSystem").setMaster("local[*]")

    // 创建Spark客户端
    val spark = SparkSession.builder().config(sparkConf).getOrCreate()
    val sc = spark.sparkContext
    val ssc = new StreamingContext(sc, Seconds(5))

    // 设置检查点目录
    ssc.checkpoint("./streaming_checkpoint")

    //数据来源于kafka
    // 获取Kafka配置
    val broker_list = ConfigurationManager.config.getString("kafka.broker.list")
    val topics = ConfigurationManager.config.getString("kafka.topics")

    // kafka消费者配置
    val kafkaParam = Map(
      "bootstrap.servers" -> broker_list,//用于初始化链接到集群的地址
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      //用于标识这个消费者属于哪个消费团体
      "group.id" -> "commerce-consumer-group",
      //如果没有初始化偏移量或者当前的偏移量不存在任何服务器上，可以使用这个配置属性
      //可以使用这个配置，latest自动重置偏移量为最新的偏移量
      "auto.offset.reset" -> "latest",
      //如果是true，则这个消费者的偏移量会在后台自动提交
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )


    // 创建DStream，返回接收到的输入数据
    // LocationStrategies：根据给定的主题和集群地址创建consumer
    // LocationStrategies.PreferConsistent：持续的在所有Executor之间分配分区
    // ConsumerStrategies：选择如何在Driver和Executor上创建和配置Kafka Consumer
    // ConsumerStrategies.Subscribe：订阅一系列主题
    //TODO  返回数据结构时什么样的?
    val adRealTimeLogDStream=KafkaUtils.createDirectStream[String,String](
                    ssc,
                    LocationStrategies.PreferConsistent,
                     ConsumerStrategies.Subscribe[String,String](Array(topics),kafkaParam))

    var adRealTimeValueDStream: DStream[String] = adRealTimeLogDStream.map(item => item.value())

    // 用于Kafka Stream的线程非安全问题，重新分区切断血统
    // TODO  ?
    adRealTimeValueDStream = adRealTimeValueDStream.repartition(400)

    // 根据动态黑名单进行数据过滤 (userid, timestamp province city userid adid)
    val filteredAdRealTimeLogDStream = filterByBlacklist(spark,adRealTimeValueDStream)

    // 业务功能一：生成动态黑名单
    generateDynamicBlacklist(filteredAdRealTimeLogDStream)
    
  }

  /**
   * @Param: filteredAdRealTimeLogDStream
   * @Return: void
   * @Author: Tom
   * @Date:  2020-03-27  22:25
   * @Description: 生成动态黑名单
  **/
  def generateDynamicBlacklist(filteredAdRealTimeLogDStream: DStream[(Long, String)]): Unit = {

    // 计算出每5个秒内的数据中，每天每个用户每个广告的点击量
    // 通过对原始实时日志的处理
    // 将日志的格式处理成<yyyyMMdd_userid_adid, 1L>格式
    val dailyUserAdClickDStream = filteredAdRealTimeLogDStream.map{ case (userid,log) =>

      // 从tuple中获取到每一条原始的实时日志
      val logSplited = log.split(" ")

      // 提取出日期（yyyyMMdd）、userid、adid
      val timestamp:String = logSplited(0)
      val date:Date = new Date(timestamp.toLong)
      val datekey:String = DateUtils.formatDateKey(date)

      val userid = logSplited(3).toLong
      val adid = logSplited(4)

      // 拼接key
      val key = datekey + "_" + userid + "_" + adid
      (key, 1L)
    }

    // 针对处理后的日志格式，执行reduceByKey算子即可，（每个batch中）每天每个用户对每个广告的点击量
    val dailyUserAdClickCountDStream:DStream[(String,Long)] = dailyUserAdClickDStream.reduceByKey(_ + _)

    // 源源不断的，每个5s的batch中，
    //当天每个用户对每支广告的点击次数 保存到数据库中
    // <yyyyMMdd_userid_adid, clickCount>
    dailyUserAdClickCountDStream.foreachRDD{ rdd =>
      rdd.foreachPartition{ items =>
        //用foreachPartition 的原因:
        // 对每个分区的数据就去获取一次连接对象
        // 每次都是从连接池中获取，而不是每次都创建
        // 写数据库操作，性能已经提到最高了

        val adUserClickCounts = ArrayBuffer[AdUserClickCount]()
        for(item <- items){
          val keySplited = item._1.split("_")
          val date = DateUtils.formatDate(DateUtils.parseDateKey(keySplited(0)))
          // yyyy-MM-dd
          val userid = keySplited(1).toLong
          val adid = keySplited(2).toLong
          val clickCount = item._2

          //批量插入
          adUserClickCounts += AdUserClickCount(date, userid,adid,clickCount)
        }
        AdUserClickCountDAO.updateBatch(adUserClickCounts.toArray)
      }
    }

    // 那么就判定这个用户就是黑名单用户，就写入mysql的表中，持久化
    val blacklistDStream = dailyUserAdClickCountDStream.filter{ case (key, count) =>
      val keySplited = key.split("_")

      // yyyyMMdd -> yyyy-MM-dd
      val date = DateUtils.formatDate(DateUtils.parseDateKey(keySplited(0)))
      val userid = keySplited(1).toLong
      val adid = keySplited(2).toLong

      // 从mysql中查询指定日期指定用户对指定广告的点击量
      val clickCount = AdUserClickCountDAO.findClickCountByMultiKey(date, userid, adid)

      // 判断，如果点击量大于等于100，ok，那么不好意思，你就是黑名单用户
      // 那么就拉入黑名单，返回true
      if(clickCount >= 100) {
        true
      }else{
        // 反之，如果点击量小于100的，那么就暂时不要管它了
        false
      }
    }

    // blacklistDStream
    // 里面的每个batch，其实就是都是过滤出来的已经在某天对某个广告点击量超过100的用户
    // 遍历这个dstream中的每个rdd，然后将黑名单用户增加到mysql中
    // 这里一旦增加以后，在整个这段程序的前面，会加上根据黑名单动态过滤用户的逻辑
    // 我们可以认为，一旦用户被拉入黑名单之后，以后就不会再出现在这里了
    // 所以直接插入mysql即可

    // 我们在插入前要进行去重
    // yyyyMMdd_userid_adid
    // 20151220_10001_10002 100
    // 20151220_10001_10003 100
    // 10001这个userid就重复了

    // 实际上，是要通过对dstream执行操作，对其中的rdd中的userid进行全局的去重, 返回Userid
    val blacklistUseridDStream:DStream[Long] = blacklistDStream.map(item => item._1.split("_")(1).toLong)

    val distinctBlacklistUseridDStream:DStream[Long] = blacklistUseridDStream.transform( uidStream => uidStream.distinct() )

    // 到这一步为止，distinctBlacklistUseridDStream
    // 每一个rdd，只包含了userid，而且还进行了全局的去重，保证每一次过滤出来的黑名单用户都没有重复的
//    distinctBlacklistUseridDStream.map{items =>
//      val adBlacklists = ArrayBuffer[AdBlacklist]()
//
//      for(item <- items)
//        adBlacklists += AdBlacklist(item)
//
//      AdBlacklistDAO.insertBatch(adBlacklists.toArray)
//    }
    //TODO  为什么要用下面的,而不是上面的?  map,transform,foreachRDD 的区别?
    distinctBlacklistUseridDStream.foreachRDD{ rdd =>
      rdd.foreachPartition{ items =>
        val adBlacklists = ArrayBuffer[AdBlacklist]()

        for(item <- items)
          adBlacklists += AdBlacklist(item)

        AdBlacklistDAO.insertBatch(adBlacklists.toArray)
      }
    }


  }


  /**
   * @Param: spark
   * @Param adRealTimeValueDStream
   * @Return: java.lang.Object
   * @Author: Tom
   * @Date:  2020-03-27  22:00
   * @Description: 根据黑名单进行过滤
  **/
  def filterByBlacklist(spark: SparkSession, adRealTimeValueDStream: DStream[String]) = {
    // 刚刚接受到原始的用户点击行为日志之后
    // 根据mysql中的动态黑名单，进行实时的黑名单过滤（黑名单用户的点击行为，直接过滤掉，不要了）
    // 使用transform算子（将dstream中的每个batch RDD进行处理，转换为任意的其他RDD，功能很强大）

    adRealTimeValueDStream.transform{consumerRecordRDD =>

      // 首先，从mysql中查询所有黑名单用户，将其转换为一个rdd
      val adBlacklists = AdBlacklistDAO.findAll()

      //黑名单转成RDD
      val blacklistRDD = spark.sparkContext.makeRDD(adBlacklists.map(item => (item.userid, true)))
      // 将原始数据rdd映射成<userid, tuple2<string, string>>
      val mappedRDD = consumerRecordRDD.map(consumerRecord => {
        val userid = consumerRecord.split(" ")(3).toLong
        (userid,consumerRecord)
      })

      // 将原始日志数据rdd，与黑名单rdd，进行左外连接
      // 如果说原始日志的userid，没有在对应的黑名单中，join不到，左外连接
      // 用inner join，内连接，会导致数据丢失
       val joinedRDD: RDD[(Long, (String, Option[Boolean]))] = mappedRDD.leftOuterJoin(blacklistRDD)

      val filteredRDD: RDD[(Long, (String, Option[Boolean]))] = joinedRDD.filter { case (userid, (log, black)) =>
        // 如果这个值存在，那么说明原始日志中的userid，join到了某个黑名单用户
        if (black.isDefined && black.get) false else true
      }

      filteredRDD.map{ case (userid,(log, black)) => (userid, log)}

    }



  }



}
