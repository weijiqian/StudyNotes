package utils

import java.security.Timestamp
import java.text.SimpleDateFormat
import java.util.{Calendar, Date, Locale}

/**
  * @Auther Tom
  * @Date 2020-03-28 22:11
  * @描述 Scala自定义日期时间转换工具类DateUtils
  */
object DateUtils {
  val DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd")
  val DATE_KEY_FORMAT = new SimpleDateFormat("yyyyMMdd")
  val TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
  val TIME_MINUTE_FORMAT = new SimpleDateFormat("yyyyMMddHHmm")
  val GMT_FORMAT = new SimpleDateFormat("dd/MMM/yyyy hh:mm:ss", Locale.ENGLISH)

  /**
    * 获取当天日期 格式:yyyy-MM-dd
    *
    * @return 当天日期
    */
  def getTodayDate: String = {
    DATE_FORMAT.format(new Date)
  }

  /**
    * 获取昨天日期 格式:yyyy-MM-dd
    *
    * @return 昨天日期
    */
  def getYesterdayDate: String = {
    val cal = Calendar.getInstance
    cal.setTime(new Date)
    cal.add(Calendar.DATE, -1)
    DATE_FORMAT.format(cal.getTime)
  }

  /** 获取日期时间 格式:yyyy-MM-dd HH:mm:ss
    *
    * @param i :  当天为0，前一天为-1,后一天为1
    * @return yyyy-MM-dd  HH:mm:ss
    */
  def getDate(i: Int): String = {
    val cal = Calendar.getInstance
    cal.setTime(new Date)
    cal.add(Calendar.DATE, i)
    TIME_FORMAT.format(cal.getTimeInMillis)
  }

  /**
    * 格式化日期 date转日期
    *
    * @param date Date对象 Sat Sep 07 03:02:01 CST 2019
    * @return yyyy-MM-dd
    */
  def formatDate(date: Date): String = DATE_FORMAT.format(date)

  /**
    * 格式化日期 时间戳转日期
    *
    * @param timestamp 时间戳
    * @return yyyy-MM-dd
    */
  def formatDate(timestamp: Timestamp): String = DATE_FORMAT.format(timestamp * 1000)

  /**
    * 格式化日期 string转日期
    *
    * @param date "yyyy-MM-dd HH:mm:ss"
    * @return yyyy-MM-dd
    */
  def formatDate(date: String): String = DATE_FORMAT.format(DATE_FORMAT.parse(date))


  /**
    * 格式化日期 string转日期
    *
    * @param date "yyyy-MM-dd HH:mm:ss"
    * @return yyyy-MM-dd
    */
  def formatDate(time: Long): String = DATE_FORMAT.format(DATE_FORMAT.format(time))


  /**
    * 格式化时间 date对象转时间
    *
    * @param date Date对象 Sat Sep 07 03:02:01 CST 2019
    * @return yyyy-MM-dd HH:mm:ss
    */
  def formatTime(date: Date): String = TIME_FORMAT.format(date)


  /**
    * 格式化日期 date转yyyyMMdd日期
    *
    * @param date yyyy-MM-dd HH:mm:ss
    * @return yyyyMMdd
    */
  def formatKeyDate(date: Date): String = DATE_KEY_FORMAT.format(date)

  /**
    * 格式化日期 date string转yyyyMMdd日期,没有-间隔
    *
    * @param date "yyyy-MM-dd HH:mm:ss"
    * @return yyyyMMdd
    */
  def formatKeyDate(date: String): String = DATE_KEY_FORMAT.format(TIME_FORMAT.parse(date))

  /**
    * 将GMT日期格式转换为时间戳
    *
    * @param gmt 07/Sep/2019:00:07:39 +0800
    * @return timestamp
    */
  def formatGmtToTimestamp(gmt: String): Long = GMT_FORMAT.parse(gmt).getTime

  /**
    * 格式化时间戳 date string转时间戳
    *
    * @param date "yyyy-MM-dd HH:mm:ss"
    * @return timestamp
    */
  def formatDateToTimestamp(date: String): Long = DATE_FORMAT.parse(date).getTime

  /**
    * 将CMT日期格式转换为时间
    *
    * @param gmt 07/Sep/2019:00:07:39 +0800
    * @return yyyy-MM-dd HH:mm:ss
    */
  def formatGmtToTime(gmt: String): String = TIME_FORMAT.format(gmt)

  /**
    * 格式化时间 保留到分钟级
    *
    * @param date
    * @return yyyyMMddHHmm
    */
  def formatTimeMinute(date: Date): String = TIME_MINUTE_FORMAT.format(date)

  /**
    * 将时间格式化为0 时整的时间
    *
    * @param date yyyy-MM-dd HH:mm:ss
    * @return yyyy-MM-dd 00:00:00
    */
  def formatTimeZone(date: String): String = {
    val time = DATE_FORMAT.parse(date).getTime
    formatDate(time)
  }

  /**
    * 将时间转换为日期格式,格式化到月
    *
    * @param date yyyy-MM-dd HH:mm:ss
    * @return yyyy-MM
    */
  def formatDateToMonth(date: String): String = {
    val sdf = new SimpleDateFormat("yyyy-MM")
    sdf.format(TIME_FORMAT.parse(date))
  }

  /**
    * 获取小时
    *
    * @param date yyyy-MM-dd HH:mm:ss
    * @return HH
    */
  def formatHour(date: String): String = {
    val sdf = new SimpleDateFormat("HH")
    sdf.format(TIME_FORMAT.parse(date))
  }
}
//————————————————
//版权声明：本文为CSDN博主「心有余力」的原创文章，遵循 CC 4.0 BY-SA 版权协议，转载请附上原文出处链接及本声明。
//原文链接：https://blog.csdn.net/lingeio/article/details/95357103
