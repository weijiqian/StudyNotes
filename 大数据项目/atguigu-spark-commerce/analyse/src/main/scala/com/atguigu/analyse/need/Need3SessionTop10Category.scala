package com.atguigu.analyse.need

import java.util.{Date, UUID}

import com.atguigu.analyse.utils.{CategorySortKey, SessionAggrStatAccumulator, Top10Category}
import com.atguigu.common.conf.ConfigurationManager
import com.atguigu.common.constant.Constants
import com.atguigu.common.model.{UserInfo, UserVisitAction}
import com.atguigu.common.utils._
import net.sf.json.JSONObject
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Dataset, SaveMode, SparkSession}
import org.apache.spark.storage.StorageLevel
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
  * @Auther Tom
  * @Date 2020-03-26 12:37
  * @描述 在符合条件的 session 中，获取点击、下单和支付数量排名前 10 的品类
  */
object Need3SessionTop10Category{


  def main(args: Array[String]): Unit = {
    //获取统计任务参数   从配置文件获取
    val jsonStr: String = ConfigurationManager.config.getString(Constants.TASK_PARAMS)
    val taskObject: JSONObject = JSONObject.fromObject(jsonStr)

    //任务的唯一标识,用在mysql中
    val taskUUID :String = UUID.randomUUID().toString;

    //构建spark
    val sparkConf:SparkConf = new SparkConf().setAppName("session").setMaster("local[*]")
    val sparkSession: SparkSession = SparkSession.builder().config(sparkConf).enableHiveSupport().getOrCreate()
    val sparkContext: SparkContext = sparkSession.sparkContext

    //查询出指定时间范围内的数据
    val actionRDD: RDD[UserVisitAction] = this.getActionRDDByDateRange(sparkSession,taskObject)

    //把数据转换为(k,v)结构
    val sessionId2ActionRDD: RDD[(String, UserVisitAction)] = actionRDD.map(item => (item.session_id,item))

    //将数据进行内存缓存
    sessionId2ActionRDD.persist(StorageLevel.MEMORY_AND_DISK)

    // 将数据转换为Session粒度， 格式为<sessionid,(sessionid,searchKeywords,clickCategoryIds,age,professional,city,sex)>
    val sessionId2AggrInfoRDD: RDD[(String, String)] = this.aggregateBySession(sparkSession,sessionId2ActionRDD)

    //准备累加器  对步长和时长进行累加
    val aggrStatAccumulator:SessionAggrStatAccumulator = new SessionAggrStatAccumulator
    sparkContext.register(aggrStatAccumulator,"aggrstat")

    //根据条件筛选出来的数据,并对步长和时长进行累加
    // 根据查询任务的配置，过滤用户的行为数据，同时在过滤的过程中，对累加器中的数据进行统计
    // filteredSessionid2AggrInfoRDD是按照年龄、职业、城市范围、性别、搜索词、点击品类这些条件过滤后的最终结果
    val filteredSessionid2AggrInfoRDD :RDD[(String,String)]= filterSessionAndAggrStat(sessionId2AggrInfoRDD,taskObject,aggrStatAccumulator)

    //对数据进行缓存
    filteredSessionid2AggrInfoRDD.persist(StorageLevel.MEMORY_AND_DISK)

    // sessionid2detailRDD，就是代表了通过筛选的session对应的访问明细数据
    // sessionid2detailRDD  是原始完整数据与（用户 + 行为数据）聚合的结果，是符合过滤条件的完整数据
    // sessionid2detailRDD ( sessionId, userAction )
    val sessionid2detailRDD:RDD[(String,UserVisitAction)] = getSessionid2detailRDD(filteredSessionid2AggrInfoRDD, sessionId2ActionRDD)

    //缓存数据 TODO  什么时候要缓存数据
    sessionid2detailRDD.persist(StorageLevel.MEMORY_AND_DISK)

    // 业务功能三：获取top10热门品类
    // 返回排名前十的品类是为了在业务功能四中进行使用
    val top10CategoryList = getTop10Category(sparkSession, taskUUID, sessionid2detailRDD)



  }



  /**
   * @Param: sparkSession
   * @Param taskUUID
   * @Param sessionid2detailRDD
   * @Return: void
   * @Author: Tom
   * @Date:  2020-03-26  12:43
   * @Description: 业务功能三：获取top10热门品类
  **/
  def getTop10Category(sparkSession: SparkSession, taskUUID: String, sessionid2detailRDD: RDD[(String, UserVisitAction)]) = {

    // 第一步: 获取所有产生过点击、下单、支付中任意行为的商品类别
    val categoryidRDD: RDD[(Long, Long)] = sessionid2detailRDD.flatMap { case (sessionId, userVisitAction) =>
      //TODO  为什么要key 和 value 要一样?
      val listBuffer: ListBuffer[(Long, Long)] = new ListBuffer[(Long, Long)]()
      // 一个session中点击的商品ID
      if (userVisitAction.click_category_id != null) {
        listBuffer += ((userVisitAction.click_category_id, userVisitAction.click_category_id))
      }

      // 一个session中下单的商品ID集合
      if (userVisitAction.order_product_ids != null) {
        for (orderCategoryId <- userVisitAction.order_category_ids.split(",")) {
          listBuffer += ((orderCategoryId.toLong, orderCategoryId.toLong))
        }
      }

      // 一个session中支付的商品ID集合
      if (userVisitAction.pay_category_ids != null) {
        for (payCategoryId <- userVisitAction.pay_category_ids.split(","))
          listBuffer += ((payCategoryId.toLong, payCategoryId.toLong))
      }
      listBuffer
    }


    // 对重复的categoryid进行去重
    // 得到了所有被点击、下单、支付的商品的品类
    val distinctCategoryIdRDD:RDD[(Long,Long)] = categoryidRDD.distinct

    // 第二步：计算各品类的点击、下单和支付的次数
    // 计算各个品类的点击次数
    val clickCategoryId2CountRDD:RDD[(Long, Long)] = getClickCategoryId2CountRDD(sessionid2detailRDD)
    // 计算各个品类的下单次数
    val orderCategoryId2CountRDD:RDD[(Long, Long)] = getOrderCategoryId2CountRDD(sessionid2detailRDD)
    // 计算各个品类的支付次数
    val payCategoryId2CountRDD:RDD[(Long, Long)] = getPayCategoryId2CountRDD(sessionid2detailRDD)

    // 第三步：join各品类与它的点击、下单和支付的次数
    // distinctCategoryIdRDD中是所有产生过点击、下单、支付行为的商品类别
    // 通过distinctCategoryIdRDD与各个统计数据的LeftJoin保证数据的完整性
    //categoryid2countRDD  (categoryid,click_count=次数|order_count=次数|pay_count=次数)
    val categoryid2countRDD = joinCategoryAndData(distinctCategoryIdRDD, clickCategoryId2CountRDD, orderCategoryId2CountRDD, payCategoryId2CountRDD);

    // 第四步：自定义二次排序key  CategorySortKey

    // 第五步：将数据映射成<CategorySortKey,info>格式的RDD，然后进行二次排序（降序）
    // 创建用于二次排序的联合key —— (CategorySortKey(clickCount, orderCount, payCount), line)
    // 按照：点击次数 -> 下单次数 -> 支付次数 这一顺序进行二次排序
    val sortKey2countRDD = categoryid2countRDD.map { case (categoryid, line) =>
      val clickCount = StringUtils.getFieldFromConcatString(line, "\\|", Constants.FIELD_CLICK_COUNT).toLong
      val orderCount = StringUtils.getFieldFromConcatString(line, "\\|", Constants.FIELD_ORDER_COUNT).toLong
      val payCount = StringUtils.getFieldFromConcatString(line, "\\|", Constants.FIELD_PAY_COUNT).toLong
      (CategorySortKey(clickCount, orderCount, payCount), line)
    }

    //降序
    val sortedCategoryCountRDD: RDD[(CategorySortKey, String)] = sortKey2countRDD.sortByKey(false)

    //取出前10个
    val top10CategoryList: Array[(CategorySortKey, String)] = sortedCategoryCountRDD.take(10)

    val top10Category:Array[Top10Category] = top10CategoryList.map { case (categorySortKey, line) =>
      val categoryid = StringUtils.getFieldFromConcatString(line, "\\|", Constants.FIELD_CATEGORY_ID).toLong
      val clickCount = StringUtils.getFieldFromConcatString(line, "\\|", Constants.FIELD_CLICK_COUNT).toLong
      val orderCount = StringUtils.getFieldFromConcatString(line, "\\|", Constants.FIELD_ORDER_COUNT).toLong
      val payCount = StringUtils.getFieldFromConcatString(line, "\\|", Constants.FIELD_PAY_COUNT).toLong

      Top10Category(taskUUID, categoryid, clickCount, orderCount, payCount)
    }

    val top10CategoryRDD: RDD[Top10Category] = sparkSession.sparkContext.makeRDD(top10Category)

    // 写入MySQL之前，将RDD转化为Dataframe
    import sparkSession.implicits._
    top10CategoryRDD.toDF().write
      .format("jdbc")
      .option("url", ConfigurationManager.config.getString(Constants.JDBC_URL))
      .option("dbtable", "top10_category")
      .option("user", ConfigurationManager.config.getString(Constants.JDBC_USER))
      .option("password", ConfigurationManager.config.getString(Constants.JDBC_PASSWORD))
      .mode(SaveMode.Append)
      .save()


    top10CategoryList
  }

  /**
   * @Param: distinctCategoryIdRDD
    * @Param clickCategoryId2CountRDD
    * @Param orderCategoryId2CountRDD
    * @Param payCategoryId2CountRDD
   * @Return: void
   * @Author: Tom
   * @Date:  2020-03-26  17:02
   * @Description: TODO
  **/
  def joinCategoryAndData(distinctCategoryIdRDD: RDD[(Long, Long)], clickCategoryId2CountRDD: RDD[(Long, Long)], orderCategoryId2CountRDD: RDD[(Long, Long)], payCategoryId2CountRDD: RDD[(Long, Long)]) = {
    // 将所有品类信息与点击次数信息结合【左连接】
    val clickJoinRDD = distinctCategoryIdRDD.leftOuterJoin(clickCategoryId2CountRDD).map { case (categoryid, (cid, optionValue)) =>
      val clickCount = if (optionValue.isDefined) optionValue.get else 0L
      val value = Constants.FIELD_CATEGORY_ID + "=" + categoryid + "|" + Constants.FIELD_CLICK_COUNT + "=" + clickCount
      (categoryid, value)
    }

    // 将所有品类信息与订单次数信息结合【左连接】
    val orderJoinRDD = clickJoinRDD.leftOuterJoin(orderCategoryId2CountRDD).map { case (categoryid, (ovalue, optionValue)) =>
      val orderCount = if (optionValue.isDefined) optionValue.get else 0L
      val value = ovalue + "|" + Constants.FIELD_ORDER_COUNT + "=" + orderCount
      (categoryid, value)
    }

    // 将所有品类信息与付款次数信息结合【左连接】
    val payJoinRDD = orderJoinRDD.leftOuterJoin(payCategoryId2CountRDD).map { case (categoryid, (ovalue, optionValue)) =>
      val payCount = if (optionValue.isDefined) optionValue.get else 0L
      val value = ovalue + "|" + Constants.FIELD_PAY_COUNT + "=" + payCount
      (categoryid, value)
    }
    payJoinRDD
  }


  /**
   * @Param: sessionid2detailRDD
   * @Return: void
   * @Author: Tom
   * @Date:  2020-03-26  14:34
   * @Description: 计算各个品类的点击次数
  **/
  def getClickCategoryId2CountRDD(sessionid2detailRDD: RDD[(String, UserVisitAction)]):RDD[(Long, Long)] = {
    //过滤,筛选出点击过的品类
    val clickCategory: RDD[(String, UserVisitAction)] = sessionid2detailRDD.filter{case (sessionId,userVisitAction) => userVisitAction.click_category_id != null}

    //(categoryid,1)
    // 获取每种类别的点击次数
    // map阶段：(品类ID，1L)
    val clickCategoryId2Count: RDD[(Long, Long)] = clickCategory.map { case (sessionId, userVisitAction) =>
      (userVisitAction.click_category_id, 1l)
    }

    // 计算各个品类的点击次数
    // reduce阶段：对map阶段的数据进行汇总
    // (品类ID1，次数) (品类ID2，次数) (品类ID3，次数) ... ... (品类ID4，次数)
    val clickCategoryId2SumCount: RDD[(Long, Long)] = clickCategoryId2Count.reduceByKey(_ + _)

    clickCategoryId2SumCount

  }

  /**
   * @Param: sessionid2detailRDD
   * @Return: java.lang.Object
   * @Author: Tom
   * @Date:  2020-03-26  14:35
   * @Description: 计算各个品类的下单次数
  **/
  def getOrderCategoryId2CountRDD(sessionid2detailRDD: RDD[(String, UserVisitAction)]):RDD[(Long,Long)] = {

      // 过滤订单数据
      val orderCategoryRDD:RDD[(String,UserVisitAction)] = sessionid2detailRDD.filter{case (sessionId,userVisitAction) => userVisitAction.order_category_ids != null}
      // 获取每种类别的下单次数
      val categoryId2OrderSum: RDD[(Long, Long)] = orderCategoryRDD.flatMap{case (sessionID,userVisitAction) => userVisitAction.order_category_ids.split(",").map(id => (id.toLong,1L))}

      // 计算各个品类的下单次数
      categoryId2OrderSum.reduceByKey(_ + _ )
    }

  /**
   * @Param: sessionid2detailRDD
   * @Return: java.lang.Object
   * @Author: Tom
   * @Date:  2020-03-26  14:35
   * @Description: 计算各个品类的支付次数
  **/
  def getPayCategoryId2CountRDD(sessionid2detailRDD: RDD[(String, UserVisitAction)]):RDD[(Long, Long)] = {
    //过滤,筛选出点击过的品类
    val payCategory: RDD[(String, UserVisitAction)] = sessionid2detailRDD.filter{case (sessionId,userVisitAction) => userVisitAction.pay_category_ids != null}

    //(categoryid,1)
    // 获取每种类别的支付次数
    // map阶段：(品类ID，1L)
    val categoryId2PaySum: RDD[(Long, Long)] = payCategory.flatMap {
      case (sessionId, userVisitAction) => {
        val arrayCategory: mutable.ArrayOps[String] = userVisitAction.order_category_ids.split(",")
        arrayCategory.map(id => (id.toLong, 1L))
      }
    }

    // 计算各个品类的支付次数
    // reduce阶段：对map阶段的数据进行汇总
    // (品类ID1，次数) (品类ID2，次数) (品类ID3，次数) ... ... (品类ID4，次数)
    val payCategoryId2SumCount: RDD[(Long, Long)] = categoryId2PaySum.reduceByKey(_ + _)

    payCategoryId2SumCount

  }




  /**
    * @MethodName: getSessionid2detailRDD
    * @Param:  * @param filteredSessionid2AggrInfoRDD
    * @param sessionId2ActionRDD
    * @Return: void
    * @Author: Tom
    * @Date:  2020-03-25  17:08
    * @Description: 过滤后的数据与没有过滤的数据 聚合 ,从而得到 RDD[(sessionId,UserVisitAction)]
    **/
  def getSessionid2detailRDD(filteredSessionid2AggrInfoRDD: RDD[(String, String)], sessionId2ActionRDD: RDD[(String, UserVisitAction)]):RDD[(String,UserVisitAction)] = {
    val value: RDD[(String, (String, UserVisitAction))] = filteredSessionid2AggrInfoRDD.join(sessionId2ActionRDD)
    value.map(item => (item._1,item._2._2))
  }



  /**
    * @MethodName: filterSessionAndAggrStat
    * @Param:  * @param sessionId2ActionRDD
    * @param taskObject
    * @param aggrStatAccumulator
    * @Return: void
    * @Author: Tom
    * @Date:  2020-03-25  16:24
    * @Description: 筛选出需要的数据,对数据进行累加
    **/
  def filterSessionAndAggrStat(sessionId2ActionRDD: RDD[(String, String)], taskParam: JSONObject, aggrStatAccumulator: SessionAggrStatAccumulator): RDD[(String,String)] = {
    //获取查询中的配置
    val startAge: String = ParamUtils.getParam(taskParam,Constants.PARAM_START_AGE)
    val endAge = ParamUtils.getParam(taskParam, Constants.PARAM_END_AGE)
    val professionals = ParamUtils.getParam(taskParam, Constants.PARAM_PROFESSIONALS)
    val cities = ParamUtils.getParam(taskParam, Constants.PARAM_CITIES)
    val sex = ParamUtils.getParam(taskParam, Constants.PARAM_SEX)
    val keywords = ParamUtils.getParam(taskParam, Constants.PARAM_KEYWORDS)
    val categoryIds = ParamUtils.getParam(taskParam, Constants.PARAM_CATEGORY_IDS)

    var _parameter = (if (startAge != null) Constants.PARAM_START_AGE + "=" + startAge + "|" else "") +
      (if (endAge != null) Constants.PARAM_END_AGE + "=" + endAge + "|" else "") +
      (if (professionals != null) Constants.PARAM_PROFESSIONALS + "=" + professionals + "|" else "") +
      (if (cities != null) Constants.PARAM_CITIES + "=" + cities + "|" else "") +
      (if (sex != null) Constants.PARAM_SEX + "=" + sex + "|" else "") +
      (if (keywords != null) Constants.PARAM_KEYWORDS + "=" + keywords + "|" else "") +
      (if (categoryIds != null) Constants.PARAM_CATEGORY_IDS + "=" + categoryIds else "")

    if (_parameter.endsWith("\\|")) {
      _parameter = _parameter.substring(0, _parameter.length() - 1)
    }

    val parameter = _parameter

    //筛选出符合条件的数据
    val filteredSessionid2AggrInfoRDD:RDD[(String,String)] = sessionId2ActionRDD.filter {
      case (sessionId, aggrInfo) =>
        //标识
        var success :Boolean = true
        if (!ValidUtils.between(aggrInfo,Constants.FIELD_AGE,parameter,Constants.FIELD_START_TIME,Constants.PARAM_END_AGE)){
          success = false
        }
        // 按照职业范围进行过滤（professionals）
        // 互联网,IT,软件
        // 互联网
        if (success && !ValidUtils.in(aggrInfo, Constants.FIELD_PROFESSIONAL, parameter, Constants.PARAM_PROFESSIONALS))
          success = false

        // 按照城市范围进行过滤（cities）
        // 北京,上海,广州,深圳
        // 成都
        if (success && !ValidUtils.in(aggrInfo, Constants.FIELD_CITY, parameter, Constants.PARAM_CITIES))
          success = false

        // 按照性别进行过滤
        // 男/女
        // 男，女
        if (success && !ValidUtils.equal(aggrInfo, Constants.FIELD_SEX, parameter, Constants.PARAM_SEX))
          success = false

        // 按照搜索词进行过滤
        // 我们的session可能搜索了 火锅,蛋糕,烧烤
        // 我们的筛选条件可能是 火锅,串串香,iphone手机
        // 那么，in这个校验方法，主要判定session搜索的词中，有任何一个，与筛选条件中
        // 任何一个搜索词相当，即通过
        if (success && !ValidUtils.in(aggrInfo, Constants.FIELD_SEARCH_KEYWORDS, parameter, Constants.PARAM_KEYWORDS))
          success = false

        // 按照点击品类id进行过滤
        if (success && !ValidUtils.in(aggrInfo, Constants.FIELD_CLICK_CATEGORY_IDS, parameter, Constants.PARAM_CATEGORY_IDS))
          success = false


        // 如果符合任务搜索需求
        if (success){
          aggrStatAccumulator.add(Constants.SESSION_COUNT)

          // 计算出session的访问时长和访问步长的范围，并进行相应的累加
          val visitLength :Long= StringUtils.getFieldFromConcatString(aggrInfo, "\\|", Constants.FIELD_VISIT_LENGTH).toLong
          val stepLength :Long= StringUtils.getFieldFromConcatString(aggrInfo, "\\|", Constants.FIELD_STEP_LENGTH).toLong
          calculateVisitLength(visitLength,aggrStatAccumulator)
          calculateStepLength(stepLength,aggrStatAccumulator)
        }
        success
    }
    filteredSessionid2AggrInfoRDD
  }


  // 计算访问时长范围
  def calculateVisitLength(visitLength: Long,sessionAggrStatAccumulator:SessionAggrStatAccumulator) {
    if (visitLength >= 1 && visitLength <= 3) {
      sessionAggrStatAccumulator.add(Constants.TIME_PERIOD_1s_3s);
    } else if (visitLength >= 4 && visitLength <= 6) {
      sessionAggrStatAccumulator.add(Constants.TIME_PERIOD_4s_6s);
    } else if (visitLength >= 7 && visitLength <= 9) {
      sessionAggrStatAccumulator.add(Constants.TIME_PERIOD_7s_9s);
    } else if (visitLength >= 10 && visitLength <= 30) {
      sessionAggrStatAccumulator.add(Constants.TIME_PERIOD_10s_30s);
    } else if (visitLength > 30 && visitLength <= 60) {
      sessionAggrStatAccumulator.add(Constants.TIME_PERIOD_30s_60s);
    } else if (visitLength > 60 && visitLength <= 180) {
      sessionAggrStatAccumulator.add(Constants.TIME_PERIOD_1m_3m);
    } else if (visitLength > 180 && visitLength <= 600) {
      sessionAggrStatAccumulator.add(Constants.TIME_PERIOD_3m_10m);
    } else if (visitLength > 600 && visitLength <= 1800) {
      sessionAggrStatAccumulator.add(Constants.TIME_PERIOD_10m_30m);
    } else if (visitLength > 1800) {
      sessionAggrStatAccumulator.add(Constants.TIME_PERIOD_30m);
    }
  }

  // 计算访问步长范围
  def calculateStepLength(stepLength: Long,sessionAggrStatAccumulator:SessionAggrStatAccumulator) {
    if (stepLength >= 1 && stepLength <= 3) {
      sessionAggrStatAccumulator.add(Constants.STEP_PERIOD_1_3);
    } else if (stepLength >= 4 && stepLength <= 6) {
      sessionAggrStatAccumulator.add(Constants.STEP_PERIOD_4_6);
    } else if (stepLength >= 7 && stepLength <= 9) {
      sessionAggrStatAccumulator.add(Constants.STEP_PERIOD_7_9);
    } else if (stepLength >= 10 && stepLength <= 30) {
      sessionAggrStatAccumulator.add(Constants.STEP_PERIOD_10_30);
    } else if (stepLength > 30 && stepLength <= 60) {
      sessionAggrStatAccumulator.add(Constants.STEP_PERIOD_30_60);
    } else if (stepLength > 60) {
      sessionAggrStatAccumulator.add(Constants.STEP_PERIOD_60);
    }
  }


  /**
    * @MethodName: aggregateBySession
    * @Param:  * @param sparkSession
    * @param sessionId2ActionRDD
    * @Return: void
    * @Author: Tom
    * @Date:  2020-03-24  15:39
    * @Description: 把数据转换  格式为<sessionid,(sessionid,searchKeywords,clickCategoryIds,age,professional,city,sex)>
    **/
  def aggregateBySession(sparkSession: SparkSession, sessionId2ActionRDD: RDD[(String, UserVisitAction)]):RDD[(String,String)] = {
    //把同一个session的数据放在一起
    val session2ListActionRDD: RDD[(String, Iterable[UserVisitAction])] = sessionId2ActionRDD.groupByKey()

    // 对每一个session分组进行聚合，将session中所有的搜索词和点击品类都聚合起来，
    // (userid,(sessionid=23232|searchKeywords=ddddd|clickCategoryIds=3333|visitlength=88|stepLength=33|startTime=2020-03-23 11:22:33))
    val userid2PartAggrInfoRDD: RDD[(Long, String)] = session2ListActionRDD.map {
      case (sessionId, actionList) => {

        //初始值,用来判断是否赋值过
        var userId: Long = -1l

        var startTime: Date = null
        var endTime: Date = null

        //StringBuffer  用来做字符串拼接,性能好些
        var searchkeywordBuffer: StringBuffer = new StringBuffer()
        var clickCategoryIdBuffer: StringBuffer = new StringBuffer()

        //步长统计
        var stepLength: Long = 0l


        //遍历session所有的访问action
        //统计 starTime,endTime,searchKeyword,clickCategoryId,stepLength
        actionList.foreach {
          userAction => {

            //确保只赋值一次,不用重复赋值
            if (userId == -1l) {
              userId = userAction.user_id
            }

            val search_keyword: String = userAction.search_keyword
            val click_category_id: Long = userAction.click_category_id

            /**
              * search_keyword  : 搜索行为
              * click_category_id : 品类点击事件
              * 这两个数据,都是可有可无的,有可能为null
              */
            if (StringUtils.isNotEmpty(search_keyword)) {
              if (!searchkeywordBuffer.toString.contains(search_keyword)) {
                searchkeywordBuffer.append(search_keyword).append(",")
              }
            }

            if (click_category_id != null && click_category_id != -1l) {
              if (!clickCategoryIdBuffer.toString.contains(click_category_id.toString)) {
                clickCategoryIdBuffer.append(click_category_id).append(",")
              }
            }

            //获取用户操作时间
            val actionTime: Date = DateUtils.parseDateKey(userAction.action_time)

            if (startTime == null) {
              startTime = actionTime
            }
            if (endTime == null) {
              endTime = actionTime
            }

            if (actionTime.before(startTime)) {
              startTime = actionTime
            }

            if (actionTime.after(endTime)) {
              endTime = actionTime
            }

            stepLength += 1
          }
        }

        //去掉最后的逗号
        val searchKeywords: String = StringUtils.trimComma(searchkeywordBuffer.toString)
        val clickCategoryIds: String = StringUtils.trimComma(clickCategoryIdBuffer.toString)

        //访问时长 (秒)
        val visitTime = (endTime.getTime - startTime.getTime) / 1000


        //拼接返回数据 格式:  key=value|key=value
        val resultStringBuffer: StringBuffer = new StringBuffer()
        resultStringBuffer.append(Constants.FIELD_SESSION_ID).append("=").append(sessionId).append("|")
          .append(Constants.FIELD_SEARCH_KEYWORDS).append("=").append(searchKeywords).append("|")
          .append(Constants.FIELD_CLICK_CATEGORY_IDS).append("=").append(clickCategoryIds).append("|")
          .append(Constants.FIELD_VISIT_LENGTH).append("=").append(visitTime).append("|")
          .append(Constants.FIELD_STEP_LENGTH).append("=").append(stepLength).append("|")
          .append(Constants.FIELD_START_TIME).append("=").append(DateUtils.formatTime(startTime)).append("|")

        val resultInfo: String = resultStringBuffer.toString()
        (userId, resultInfo)
      }

    }

    // 查询所有用户数据，并映射成<userid,Row>的格式
    import sparkSession.implicits._
    val userid2InfoRDD = sparkSession.sql("select * from user_info").as[UserInfo].rdd.map(item => (item.user_id, item))

    // 将session粒度聚合数据，与用户信息进行join
    val userid2FullInfoRDD = userid2PartAggrInfoRDD.join(userid2InfoRDD);

    // 对join起来的数据进行拼接，并且返回<sessionid,fullAggrInfo>格式的数据
    val sessionid2FullAggrInfoRDD = userid2FullInfoRDD.map { case (uid, (partAggrInfo, userInfo)) =>
      val sessionid = StringUtils.getFieldFromConcatString(partAggrInfo, "\\|", Constants.FIELD_SESSION_ID)

      val fullAggrInfo = partAggrInfo + "|" +
        Constants.FIELD_AGE + "=" + userInfo.age + "|" +
        Constants.FIELD_PROFESSIONAL + "=" + userInfo.professional + "|" +
        Constants.FIELD_CITY + "=" + userInfo.city + "|" +
        Constants.FIELD_SEX + "=" + userInfo.sex

      (sessionid, fullAggrInfo)
    }

    sessionid2FullAggrInfoRDD



  }


  /**
    * 查询出指定时间范围内的数据
    * @param sparkSession
    * @param taskObject
    */
  def getActionRDDByDateRange(sparkSession: SparkSession, taskObject: JSONObject): RDD[UserVisitAction] = {
    val startDate: String = ParamUtils.getParam(taskObject,Constants.PARAM_START_DATE)
    val endDate: String = ParamUtils.getParam(taskObject,Constants.PARAM_END_DATE)

    //使用spark SQL 插叙数据
    import sparkSession.implicits._
    val dataFrame: DataFrame = sparkSession.sql("select * from user_visit_action where date>='" + startDate + "'and date<='" + endDate + "'")
    val dataSet: Dataset[UserVisitAction] = dataFrame.as[UserVisitAction]
    val rdd: RDD[UserVisitAction] = dataSet.rdd
    rdd
  }

}

