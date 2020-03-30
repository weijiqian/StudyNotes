package utils

import java.text.SimpleDateFormat
import java.util.{Calendar, Date}

import scala.collection.mutable.ArrayBuffer

/**
  * @Auther Tom
  * @Date 2020-03-28 22:22
  * @描述 时间类
  */
object TimeUtils {

  /**
    * 获取传入日期与当前日期的天数差
    *
    * @param day          待比较日期(仅包含月日)
    * @param dayFormatted 待比较日期格式(例如:MMdd或MM-dd)
    * @return
    */
  def daysBetweenToday(day: String, dayFormatted: String): Int = {
    val calendar = Calendar.getInstance
    calendar.setTime(new Date())
    val today = calendar.get(Calendar.DAY_OF_YEAR)
    val year = calendar.get(Calendar.YEAR)
    val inputF = new SimpleDateFormat("yyyy" + dayFormatted)
    val date = inputF.parse(year + day)
    calendar.setTime(date)
    val inputDay = calendar.get(Calendar.DAY_OF_YEAR)
    var days = today - inputDay
    if (days < 0) {
      val beforeYearDate = inputF.parse((year - 1) + day)
      calendar.setTime(beforeYearDate)
      days = calendar.getActualMaximum(Calendar.DAY_OF_YEAR) - calendar.get(Calendar.DAY_OF_YEAR) + today
    }
    return days
  }

  /**
    * 将毫秒级时间戳转化成分钟级时间戳
    *
    * @param time 毫秒级时间戳
    * @return 分钟级时间戳
    */
  def getMinTimestamp(time: Long): Long = {
    val minTime = time / (1000 * 60)
    minTime
  }

  /**
    * 将时间字符串修改为格式
    *
    * @param inpuTime        输入时间
    * @param inputFormatted  输入时间格式
    * @param outputFormatted 输出时间格式
    * @return
    */
  def formatTime(inpuTime: String, inputFormatted: String, outputFormatted: String): String = {
    val inputF = new SimpleDateFormat(inputFormatted)
    val outputF = new SimpleDateFormat(outputFormatted)
    val inputT = inputF.parse(inpuTime)
    outputF.format(inputT)
  }


  /**
    * 获取传入时间戳的天数差
    *
    * @param t1 较小时间戳
    * @param t2 较大时间戳
    * @return
    */
  def caculate2Days(t1: Long, t2: Long): Int = {
    import java.util.Calendar
    val calendar = Calendar.getInstance
    calendar.setTimeInMillis(t2)
    val t2Day = calendar.get(Calendar.DAY_OF_YEAR)
    calendar.setTimeInMillis(t1)
    val t1Day = calendar.get(Calendar.DAY_OF_YEAR)
    var days = t2Day - t1Day
    if (days < 0) {
      days = calendar.getActualMaximum(Calendar.DAY_OF_YEAR) - t1Day + t2Day
    }
    return days;
  }

  /**
    * 判断nowTime是否在startTime与endTime之间
    * @param nowTime
    * @param startTime
    * @param endTime
    * @param formater
    * @return
    */
  def isBetweenDate(nowTime: String, startTime: String, endTime: String, formater: String): Boolean = {
    val df = new SimpleDateFormat(formater)
    val nowDate = df.parse(nowTime)
    val startDate = df.parse(startTime)
    val endDate = df.parse(endTime)
    if ((nowDate.getTime == startDate.getTime) || (nowDate.getTime == endDate.getTime)) return true
    if (nowDate.after(startDate) && nowDate.before(endDate)) {
      true
    } else {
      false
    }
  }




  /**
    * 时间增加天数，返回时间
    *
    * @param date 入参时间
    * @param num  增加的天数
    * @return
    */
  def funAddDate(date: String, num: Int): String = {
    val myformat = new SimpleDateFormat("yyyyMMdd")
    var dnow = new Date()
    dnow = myformat.parse(date)
    val cal = Calendar.getInstance()
    cal.setTime(dnow)
    cal.add(Calendar.DAY_OF_MONTH, num)
    val newday = cal.getTime
    myformat.format(newday)
  }

  /**
    * 时间增加小时，返回时间
    *
    * @param date 入参时间
    * @param num  增加的天数
    * @return
    */
  def funAddHour(date: String, num: Int): String = {
    val myformat = new SimpleDateFormat("yyyyMMddHH")
    var dnow = new Date()
    dnow = myformat.parse(date)
    val cal = Calendar.getInstance()
    cal.setTime(dnow)
    cal.add(Calendar.HOUR, num)
    val newday = cal.getTime
    myformat.format(newday)
  }

  /**
    * 时间增加分钟，返回时间
    *
    * @param date 入参时间
    * @param num  增加的分钟数
    * @return
    */
  def funAddMinute(date: String, num: Int): String = {
    val myformat = new SimpleDateFormat("yyyyMMddHHmm")
    var dnow = new Date()
    dnow = myformat.parse(date)
    val cal = Calendar.getInstance()
    cal.setTime(dnow)
    cal.add(Calendar.MINUTE, num)
    val newday = cal.getTime
    myformat.format(newday)
  }
  /**
    * 时间增加秒，返回时间
    *
    * @param date 入参时间
    * @param num  增加的秒数
    * @return
    */
  def funAddSecond(date: String, num: Int, format: String): String = {
    val myformat = new SimpleDateFormat(format)
    var dnow = new Date()
    dnow = myformat.parse(date)
    val cal = Calendar.getInstance()
    cal.setTime(dnow)
    cal.add(Calendar.SECOND, num)
    val newday = cal.getTime
    myformat.format(newday)
  }


  /** 获取过去几天的时间数组
    *
    * @param date
    * @param num
    * @return
    */
  def getPastDays(date: String, num: Int): Array[String] = {
    val buffer = new ArrayBuffer[String]()
    val range = 0 until num
    for(i <- range){
      buffer.append(funAddDate(date,-i))
    }
    buffer.toArray
  }

  /** 获取过去几小时的时间数组
    *
    * @param date
    * @param num
    * @return
    */
  def getPastHours(date: String, num: Int, interval:Int): Array[String] = {
    val buffer = new ArrayBuffer[String]()
    val range = 0 until num
    for(i <- range){
      buffer.append(funAddHour(date,-i*interval))
    }
    buffer.toArray
  }


}
