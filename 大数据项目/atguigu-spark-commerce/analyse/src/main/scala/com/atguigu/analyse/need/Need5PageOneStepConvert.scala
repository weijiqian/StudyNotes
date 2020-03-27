package com.atguigu.analyse.need

import java.util.UUID

import com.atguigu.analyse.utils.PageSplitConvertRate
import com.atguigu.common.conf.ConfigurationManager
import com.atguigu.common.constant.Constants
import com.atguigu.common.model.UserVisitAction
import com.atguigu.common.utils.{DateUtils, NumberUtils, ParamUtils}
import net.sf.json.JSONObject
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.storage.StorageLevel
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable

/**
  * @Auther Tom
  * @Date 2020-03-27 16:39
  * @描述
  * 页面单跳转化率模块spark作业
  *
  *  页面转化率的求解思路是通过UserAction表获取一个session的所有UserAction，根据时间顺序排序后获取全部PageId
  *  然后将PageId组合成PageFlow，即1,2,3,4,5的形式（按照时间顺序排列），之后，组合为1_2, 2_3, 3_4, ...的形式
  *  然后筛选出出现在targetFlow中的所有A_B
  *
  *  对每个A_B进行数量统计，然后统计startPage的PV，之后根据targetFlow的A_B顺序，计算每一层的转化率
  */
object Need5PageOneStepConvert {



  def main(args: Array[String]): Unit = {
    // 获取统计任务参数【为了方便，直接从配置文件中获取，企业中会从一个调度平台获取】
    val jsonStr = ConfigurationManager.config.getString(Constants.TASK_PARAMS)
    val taskParam = JSONObject.fromObject(jsonStr)

    // 任务的执行ID，用户唯一标示运行后的结果，用在MySQL数据库中
    val taskUUID = UUID.randomUUID().toString

    // 构建Spark上下文
    val sparkConf = new SparkConf().setAppName("SessionAnalyzer").setMaster("local[*]")

    // 创建Spark客户端
    val sparkSession:SparkSession = SparkSession.builder().config(sparkConf).enableHiveSupport().getOrCreate()
    val sparkContext:SparkContext = sparkSession.sparkContext

    // 查询指定日期范围内的用户访问行为数据
    val actionRDD = this.getActionRDDByDateRange(sparkSession, taskParam)

    val sessionId2ActionBeanRDD: RDD[(String, UserVisitAction)] = actionRDD.map(item => (item.session_id,item))

    // 将数据进行内存缓存
    sessionId2ActionBeanRDD.persist(StorageLevel.MEMORY_AND_DISK)

    // 对<sessionid,访问行为> RDD，做一次groupByKey操作，生成页面切片
    val sessionId2ActionListRDD: RDD[(String, Iterable[UserVisitAction])] = sessionId2ActionBeanRDD.groupByKey()

    // 最核心的一步，每个session的单跳页面切片的生成，以及页面流的匹配，算法
    // 返回：(1_2, 1)，(3_4, 1), ..., (100_101, 1)
    val pageSplitRDD:RDD[List[(String, Int)]] = generateAndMatchPageSplit(sparkContext, sessionId2ActionListRDD, taskParam)

    //flatmap 把嵌套集合拆开
    val pageSplitRDD2: RDD[(String, Int)] = pageSplitRDD.flatMap(item => item)

    // 统计每个跳转切片的总个数
    // pageSplitPvMap：(1_2, 102320), (3_4, 90021), ..., (100_101, 45789)
    val pageSplitPvMap: collection.Map[String, Long] = pageSplitRDD2.countByKey()

    // 首先计算首页PV的数量
    val startPagePv:Long = getStartPagePv(taskParam, sessionId2ActionListRDD)

    // 计算目标页面流的各个页面切片的转化率
    val convertRateMap = computePageSplitConvertRate(taskParam, pageSplitPvMap, startPagePv)

    // 持久化页面切片转化率
    persistConvertRate(sparkSession, taskUUID, convertRateMap)

    sparkSession.close()

  }

  /**
   * @Param: sparkSession
   * @Param taskUUID
   * @Param convertRateMap
   * @Return: void
   * @Author: Tom
   * @Date:  2020-03-27  18:26
   * @Description: 保存到数据库
  **/
  def persistConvertRate(sparkSession: SparkSession, taskUUID: String, convertRateMap: mutable.HashMap[String, Double]) = {
    val convertRate = convertRateMap.map(item => item._1 + "=" + item._2).mkString("|")

    val pageSplitConvertRateRDD = sparkSession.sparkContext.makeRDD(Array(PageSplitConvertRate(taskUUID,convertRate)))

    import sparkSession.implicits._
    pageSplitConvertRateRDD.toDF().write
      .format("jdbc")
      .option("url", ConfigurationManager.config.getString(Constants.JDBC_URL))
      .option("dbtable", "page_split_convert_rate")
      .option("user", ConfigurationManager.config.getString(Constants.JDBC_USER))
      .option("password", ConfigurationManager.config.getString(Constants.JDBC_PASSWORD))
      .mode(SaveMode.Append)
      .save()
  }


  /**
   * @Param: taskParam
    * @Param pageSplitPvMap  页面切片点击量
   * @Param startPagePv  起始页面点击量
   * @Return: void
   * @Author: Tom
   * @Date:  2020-03-27  18:16
   * @Description: 计算页面切片转化率
  **/
  def computePageSplitConvertRate(taskParam: JSONObject, pageSplitPvMap: collection.Map[String, Long], startPagePv: Long) = {
    val convertRateMap = new mutable.HashMap[String, Double]()
    //1,2,3,4,5,6,7
    val targetPageFlow = ParamUtils.getParam(taskParam, Constants.PARAM_TARGET_PAGE_FLOW)
    val targetPages = targetPageFlow.split(",").toList
    //(1_2,2_3,3_4,4_5,5_6,6_7)
    val targetPagePairs = targetPages.slice(0, targetPages.length-1).zip(targetPages.tail).map(item => item._1 + "_" + item._2)

    // lastPageSplitPv：存储最新一次的页面PV数量
    var lastPageSplitPv = startPagePv.toDouble
    // 3,5,2,4,6
    // 3_5
    // 3_5 pv / 3 pv
    // 5_2 rate = 5_2 pv / 3_5 pv

    // 通过for循环，获取目标页面流中的各个页面切片（pv）
    for(targetPage <- targetPagePairs){
      // 先获取pageSplitPvMap中记录的当前targetPage的数量
      val targetPageSplitPv = pageSplitPvMap.get(targetPage).get.toDouble
      println((targetPageSplitPv, lastPageSplitPv))
      // 用当前targetPage的数量除以上一次lastPageSplit的数量，得到转化率
      val convertRate = NumberUtils.formatDouble(targetPageSplitPv / lastPageSplitPv, 2)
      // 对targetPage和转化率进行存储
      convertRateMap.put(targetPage, convertRate)
      // 将本次的targetPage作为下一次的lastPageSplitPv
      lastPageSplitPv = targetPageSplitPv
    }

    convertRateMap
  }


  /**
   * @Param: taskParam
   * @Param sessionId2ActionListRDD
   * @Return: void
   * @Author: Tom
   * @Date:  2020-03-27  18:09
   * @Description: 计算首页的点击量
  **/
  def getStartPagePv(taskParam: JSONObject,
                     sessionId2ActionListRDD: RDD[(String, Iterable[UserVisitAction])]):Long = {
    // 获取配置文件中的targetPageFlow
    val targetPageFlow = ParamUtils.getParam(taskParam, Constants.PARAM_TARGET_PAGE_FLOW)
    // 获取起始页面ID
    val startPageId = targetPageFlow.split(",")(0).toLong

    // sessionid2actionsRDD是聚合后的用户行为数据
    // userVisitAction中记录的是在一个页面中的用户行为数据
    val startPageRDD: RDD[UserVisitAction] = sessionId2ActionListRDD.flatMap { case (sessionId, actionList) =>
      actionList.filter(item => item.page_id == startPageId)
    }
    startPageRDD.count()
  }


  /**
   * @Param: sparkContext
   * @Param sessionId2ActionListRDD
   * @Param taskParam
   * @Return: (1_2, 1)，(3_4, 1), ..., (100_101, 1)
   * @Author: Tom
   * @Date:  2020-03-27  16:51
   * @Description: 页面切片生成与匹配算法
    *               注意，一开始我们只有UserAciton信息，
    *               通过将UserAction按照时间进行排序，
    *               然后提取PageId，再进行连接，可以得到PageFlow
  **/
  def generateAndMatchPageSplit(sparkContext: SparkContext,
                                sessionId2ActionListRDD: RDD[(String, Iterable[UserVisitAction])],
                                taskParam: JSONObject):RDD[List[(String, Int)]] = {
    //@return "1,2,3,4,5,6,7"
    val targetPageFlow = ParamUtils.getParam(taskParam, Constants.PARAM_TARGET_PAGE_FLOW)
    //将字符串转换成为了List[String]
    //需要查询的页面
    val targetPages:List[String] = targetPageFlow.split(",").toList

    //生成两个列表,用于组合
    // @return List(1,2,3,4,5,6)
    val targetPages1:List[String] = targetPages.slice(0,targetPages.length -1)
    //@return List(2,3,4,5,6,7)
    val targetPages2:List[String] = targetPages.slice(1,targetPages.length)
    //@return List((1,2),(2,3),(3,4),(4,5),(5,6),(6,7))
    val targetPages3: List[(String, String)] = targetPages1.zip(targetPages2)
    //@return (1_2,2_3,3_4,4_5,5_6,6_7)
    val targetPagesPairs: List[String] = targetPages3.map(item => item._1 + "_" + item._2)

    //因为下面的算子里面要用到这个数据 ,所以转为广播变量
    val targetPageFlowBroadcast: Broadcast[List[String]] = sparkContext.broadcast(targetPagesPairs)


    val value: RDD[List[(String, Int)]] = sessionId2ActionListRDD.map { case (sessionId, actionList) =>

      //按照点击事件排序
      //自然顺序,从小到大排序
      val sortUserActionList: List[UserVisitAction] = actionList.toList.sortBy(action => DateUtils.parseTime(action.action_time).getTime)
      //提取pageId 信息
      val sortPageIdList: List[Long] = sortUserActionList.map(item =>
        if (item.page_id != null) {
          item.page_id
        } else {
          0
        }
      )
      val pageIdList: List[Long] = sortPageIdList.filter(item => item != 0)

      //生成切片
      val pageIdList2: List[Long] = pageIdList.slice(0, pageIdList.length - 1)
      val pageIdList3: List[Long] = pageIdList.slice(1, pageIdList.length)
      val pageIdList4: List[(Long, Long)] = pageIdList2.zip(pageIdList3)
      val pagePairs: List[String] = pageIdList4.map(item => item._1 + "_" + item._2)
      // 只要是当前session的PageFlow有一个切片与targetPageFlow中任一切片重合，那么就保留下来
      // 目标：(1_2,2_3,3_4,4_5,5_6,6_7)   当前：(1_2,2_5,5_6,6_7,7_8)
      // 最后保留：(1_2,5_6,6_7)
      // 输出：(1_2, 1) (5_6, 1) (6_7, 1)
      val needPagePairs: List[String] = pagePairs.filter(item => targetPageFlowBroadcast.value.contains(item))
      needPagePairs.map(item => (item, 1))

    }
    value

  }



  /**
   * @Param: spark
   * @Param taskParam
   * @Return: RDD<UserVisitAction>
   * @Author: Tom
   * @Date:  2020-03-27  16:43
   * @Description:  查询指定日期范围内的用户访问行为数据
  **/
  def getActionRDDByDateRange(spark:SparkSession, taskParam:JSONObject): RDD[UserVisitAction] = {
    val startDate = ParamUtils.getParam(taskParam, Constants.PARAM_START_DATE)
    val endDate = ParamUtils.getParam(taskParam, Constants.PARAM_END_DATE)
    import spark.implicits._
    spark.sql("select * from user_visit_action where date>='" + startDate + "' and date<='" + endDate + "'")
      .as[UserVisitAction].rdd
  }
}
