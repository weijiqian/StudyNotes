package com.atguigu.analyse.need

import java.sql.Date

import com.atguigu.analyse.utils._
import com.atguigu.common.conf.ConfigurationManager
import com.atguigu.common.utils.DateUtils
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.{Minutes, Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}

import scala.collection.mutable.ArrayBuffer


/**
  * @Auther Tom
  * @Date 2020-03-27 21:41
  * @描述 需求七:广告黑名单实时统计
  *    将每天对某个广告点击超过 100 次的用户拉黑
  */
object Need7ADBlackList {
  private val checkpoint = "./streaming_checkpoint"

  def main(args: Array[String]): Unit = {

    def createSSC(): StreamingContext = {
      // 构建Spark上下文
      val sparkConf = new SparkConf().setAppName("streamingRecommendingSystem").setMaster("local[*]")

      // 创建Spark客户端
      val spark = SparkSession.builder().config(sparkConf).getOrCreate()
      val sc = spark.sparkContext

      val ssc = new StreamingContext(sparkConf, Seconds(5))
      // 设置检查点目录
      ssc.checkpoint(checkpoint)



      /**
        *输入处理步骤
        */
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
      //@return  DStream[RDD,RDD,RDD ,RDD ]  RDD[(key,value),(key,value),(key,value)]
      // DStream 里面是RDD,RDD里面是kv对.
      val adRealTimeLogDStream=KafkaUtils.createDirectStream[String,String](
        ssc,
        LocationStrategies.PreferConsistent,
        ConsumerStrategies.Subscribe[String,String](Array(topics),kafkaParam))

      //@return  DStream[RDD]  RDD[String]
      //String => timestamp  province  city  userid  adid
      var adRealTimeValueDStream: DStream[String] = adRealTimeLogDStream.map(item => item.value())

      // 用于Kafka Stream的线程非安全问题，重新分区切断血统
      // TODO  ?
      adRealTimeValueDStream = adRealTimeValueDStream.repartition(400)

      // 根据动态黑名单进行数据过滤 (userid, timestamp province city userid adid)
      val filteredAdRealTimeLogDStream = filterByBlacklist(spark,adRealTimeValueDStream)

      // 业务功能一：生成动态黑名单
      generateDynamicBlacklist(filteredAdRealTimeLogDStream)

      // 业务功能二：计算广告点击流量实时统计结果（yyyyMMdd_province_city_adid,clickCount）
      val adRealTimeStatDStream = calculateRealTimeStat(filteredAdRealTimeLogDStream)


      // 业务功能三：实时统计每天每个省份top3热门广告
      calculateProvinceTop3Ad(spark,adRealTimeStatDStream)

      // 业务功能四：实时统计每天每个广告在最近1小时的滑动窗口内的点击趋势（每分钟的点击量）
      calculateAdClickCountByWindow(adRealTimeValueDStream)

      ssc
    }

    //生产环境是这样创建的.
    val ssc:StreamingContext = StreamingContext.getActiveOrCreate(checkpoint,createSSC)

    ssc.start()
    ssc.awaitTermination()
  }

  /**
   * @Param: adRealTimeValueDStream
   * @Return: void
   * @Author: Tom
   * @Date:  2020-03-28  14:31
   * @Description: 业务功能四：实时统计每天每个广告在最近1小时的滑动窗口内的点击趋势（每分钟的点击量）
  **/
  def calculateAdClickCountByWindow(adRealTimeValueDStream: DStream[String]) = {
    // 映射成<yyyyMMddHHMM_adid,1L>格式
    val pairDStream = adRealTimeValueDStream.map{ case consumerRecord  =>
      val logSplited = consumerRecord.split(" ")
      val timeMinute = DateUtils.formatTimeMinute(new Date(logSplited(0).toLong))
      val adid = logSplited(4).toLong

      (timeMinute + "_" + adid, 1L)
    }


    //对滑动窗口中新的时间间隔内数据增加聚合并移去最早的与新增数据量的时间间隔内的数据统计量
    //Minutes(60)表示窗口的宽度   Seconds(10)表示多久滑动一次(滑动的时间长度)
    //综上所示：每10s进行批处理一次，窗口时间为1hour范围内，每10s进行滑动一次
    //批处理时间<=滑动时间<=窗口时间，如果三个时间相等，表示默认处理单个的time节点
    // 计算窗口函数，1小时滑动窗口内的广告点击趋势
    //@return  是1小时内的总数据
    val aggrRDD: DStream[(String, Long)] = pairDStream.reduceByKeyAndWindow((a:Long,b:Long) => (a+b),Minutes(60l),Seconds(10l))

    // 上一步reduceByKey ,已经把相同key的数据合并了
    // 最近1小时内，各分钟的点击量，并保存到数据库
    aggrRDD.foreachRDD{rdd =>

      //用 foreachPartition  而不用 foreach 是为了一个分区保存一次数据.
      //避免多次操作.
      rdd.foreachPartition{items:Iterator[(String,Long)] =>
        //一个rdd里面的数据  汇总数据.
        val adClickTrends = ArrayBuffer[AdClickTrend]()
        for ((timeMinuteAdid,count) <- items){
          val keySplited:Array[String] = timeMinuteAdid.split("_")
          val dateMinute:String = keySplited(0)
          val adid = keySplited(1).toLong

          val date = DateUtils.formatDate(DateUtils.parseDateKey(dateMinute.substring(0, 8)))
          val hour = dateMinute.substring(8, 10)
          val minute = dateMinute.substring(10)

          adClickTrends += AdClickTrend(date,hour,minute,adid,count)
        }
        //一个分区的数据保存一次,避免多次操作
        AdClickTrendDAO.updateBatch(adClickTrends.toArray)
      }


    }

  }



  /**
   * @Param: spark
   * @Param adRealTimeStatDStream  每天此刻的累计数据
   * @Return: void
   * @Author: Tom
   * @Date:  2020-03-28  13:29
   * @Description: 业务功能三：计算每天各省份的top3热门广告
  **/
  def calculateProvinceTop3Ad(spark: SparkSession, adRealTimeStatDStream: DStream[(String, Long)]): Unit = {

    val mapDSream: DStream[(String, Long)] = adRealTimeStatDStream.map { case (keyString, count) =>

      val keySplited = keyString.split("_")
      val date = keySplited(0)
      val province = keySplited(1)
      val adid = keySplited(3).toLong
      val clickCount = count

      // 计算出每天各省份各广告的点击量
      val key = date + "_" + province + "_" + adid
      (key, clickCount)

    }
    val dailyAdClickCountByProvinceDStream: DStream[(String, Long)] = mapDSream.reduceByKey(_ + _)
    // 将dailyAdClickCountByProvinceRDD转换为DataFrame
    // 注册为一张临时表
    // 使用Spark SQL，通过开窗函数，获取到各省份的top3热门广告
    val rowDStream: DStream[(String, String, Long, Long)] = dailyAdClickCountByProvinceDStream.map { case (keyString, count) =>

      val keySplited = keyString.split("_")
      val datekey = keySplited(0)
      val province = keySplited(1)
      val adid = keySplited(2).toLong
      val clickCount = count

      val date = DateUtils.formatDate(DateUtils.parseDateKey(datekey))

      (date, province, adid, clickCount)

    }

    val rowsDStream: DStream[Row] = rowDStream.transform { rdd =>

      import spark.implicits._
      val dailyAdClickCountByProvinceDF: DataFrame = rdd.toDF("date", "province", "ad_id", "click_count")

      // 将dailyAdClickCountByProvinceDF，注册成一张临时表
      dailyAdClickCountByProvinceDF.createOrReplaceTempView("tmp_daily_ad_click_count_by_prov")
      // 使用Spark SQL执行SQL语句，配合开窗函数，统计出各身份top3热门的广告
      val provinceTop3AdDF = spark.sql(
        "SELECT "
          + "date,"
          + "province,"
          + "ad_id,"
          + "click_count "
          + "FROM ( "
          + "SELECT "
          + "date,"
          + "province,"
          + "ad_id,"
          + "click_count,"
          + "ROW_NUMBER() OVER(PARTITION BY province ORDER BY click_count DESC) rank "
          + "FROM tmp_daily_ad_click_count_by_prov "
          + ") t "
          + "WHERE rank<=3"
      )
      provinceTop3AdDF.rdd
    }
    // 每次都是刷新出来各个省份最热门的top3广告，将其中的数据批量更新到MySQL中
    rowsDStream.foreachRDD{ rdd =>
      rdd.foreachPartition{ items =>

        // 插入数据库
        val adProvinceTop3s = ArrayBuffer[AdProvinceTop3]()

        for (item <- items){
          val date = item.getString(0)
          val province = item.getString(1)
          val adid = item.getLong(2)
          val clickCount = item.getLong(3)
          adProvinceTop3s += AdProvinceTop3(date,province,adid,clickCount)
        }
        AdProvinceTop3DAO.updateBatch(adProvinceTop3s.toArray)

      }
    }



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

  /**
    * @Param: filteredAdRealTimeLogDStream
    * @Return: void
    * @Author: Tom
    * @Date:  2020-03-28  10:03
    * @Description: 业务功能二：计算广告点击流量实时统计
    **/
  def calculateRealTimeStat(filteredAdRealTimeLogDStream: DStream[(Long, String)]) = {
    // 计算每天各省各城市各广告的点击量
    // 设计出来几个维度：日期、省份、城市、广告
    // 2015-12-01，当天，可以看到当天所有的实时数据（动态改变），比如江苏省南京市
    // 广告可以进行选择（广告主、广告名称、广告类型来筛选一个出来）
    // 拿着date、province、city、adid，去mysql中查询最新的数据
    // 等等，基于这几个维度，以及这份动态改变的数据，是可以实现比较灵活的广告点击流量查看的功能的

    // date province city userid adid
    // date_province_city_adid，作为key；1作为value
    // 通过spark，直接统计出来全局的点击次数，在spark集群中保留一份；在mysql中，也保留一份
    // 我们要对原始数据进行map，映射成<date_province_city_adid,1>格式
    // 然后呢，对上述格式的数据，执行updateStateByKey算子
    // spark streaming特有的一种算子，在spark集群内存中，维护一份key的全局状态

    val mappedDStream:DStream[(String,Long)] = filteredAdRealTimeLogDStream.map{ case (userid, log) =>
      val logSplited = log.split(" ")

      val timestamp = logSplited(0)
      val date = new Date(timestamp.toLong)
      val datekey = DateUtils.formatDateKey(date)

      val province = logSplited(1)
      val city = logSplited(2)
      val adid = logSplited(4).toLong

      val key = datekey + "_" + province + "_" + city + "_" + adid

      (key, 1L)
    }

    // 在这个dstream中，就相当于，有每个batch rdd累加的各个key（各天各省份各城市各广告的点击次数）
    // 每次计算出最新的值，就在aggregatedDStream中的每个batch rdd中反应出来

    val aggregatedDStream = mappedDStream.updateStateByKey[Long]{ case (values:Seq[Long], old:Option[Long]) =>
      // 举例来说
      // 对于每个key，都会调用一次这个方法
      // 比如key是<20151201_Jiangsu_Nanjing_10001,1>，就会来调用一次这个方法7
      // 10个

      // values，(1,1,1,1,1,1,1,1,1,1)

      // 首先根据optional判断，之前这个key，是否有对应的状态
      var clickCount = 0L

      // 如果说，之前是存在这个状态的，那么就以之前的状态作为起点，进行值的累加
      if(old.isDefined) {
        clickCount = old.get
      }

      // values，代表了，batch rdd中，每个key对应的所有的值
      for(value <- values) {
        clickCount += value
      }

      Some(clickCount)
    }

    // 将计算出来的最新结果，同步一份到mysql中，以便于j2ee系统使用
    // TODO  为什么要用foreachRDD
    aggregatedDStream.foreachRDD{ rdd =>

      rdd.foreachPartition{ items =>

        //批量保存到数据库
        val adStats = ArrayBuffer[AdStat]()

        for(item <- items){
          val keySplited = item._1.split("_")
          val date = keySplited(0)
          val province = keySplited(1)
          val city = keySplited(2)
          val adid = keySplited(3).toLong

          val clickCount = item._2
          adStats += AdStat(date,province,city,adid,clickCount)
        }
        AdStatDAO.updateBatch(adStats.toArray)

      }

    }
    aggregatedDStream
  }




}
