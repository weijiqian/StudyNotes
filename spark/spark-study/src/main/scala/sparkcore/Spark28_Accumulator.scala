package sparkcore

import org.apache.spark.{SparkConf, SparkContext}

/**
  * @Auther Tom
  * @Date 2020-03-25 16:00
  * @描述 累加器
  */
object Spark28_Accumulator {

  def main(args: Array[String]) {
    val conf=new SparkConf().setAppName("LogAccumulator")
    val sc=new SparkContext(conf)

    val accum = new LogAccumulator
    sc.register(accum, "logAccum")
    val sum = sc.parallelize(Array("1", "2a", "3", "4b", "5", "6", "7cd", "8", "9"), 2).filter(line => {
      val pattern = """^-?(\d+)"""
      val flag = line.matches(pattern)
      if (!flag) {
        accum.add(line)
      }
      flag
    }).map(_.toInt).reduce(_ + _)

    println("sum: " + sum)
    for (v <- accum.value) print(v + "")
    println()
    sc.stop()
  }
}

/**
  * 自定义 累加器  统计不包含字母的
  */
class LogAccumulator extends org.apache.spark.util.AccumulatorV2[String, java.util.Set[String]] {

  private val _logArray: java.util.Set[String] = new java.util.HashSet[String]()

  override def isZero: Boolean = {
    _logArray.isEmpty
  }

  override def reset(): Unit = {
    _logArray.clear()
  }

  override def add(v: String): Unit = {
    _logArray.add(v)
  }

  override def merge(other: org.apache.spark.util.AccumulatorV2[String, java.util.Set[String]]): Unit = {
    other match {
      case o: LogAccumulator => _logArray.addAll(o.value)
    }

  }

  override def value: java.util.Set[String] = {
    java.util.Collections.unmodifiableSet(_logArray)
  }

  override def copy():org.apache.spark.util.AccumulatorV2[String, java.util.Set[String]] = {
    val newAcc = new LogAccumulator()
    _logArray.synchronized{
      newAcc._logArray.addAll(_logArray)
    }
    newAcc
  }
}
