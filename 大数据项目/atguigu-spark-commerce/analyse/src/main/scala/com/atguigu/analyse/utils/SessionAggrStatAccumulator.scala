package com.atguigu.analyse.utils

import org.apache.spark.util.AccumulatorV2

import scala.collection.mutable

/**
  * @Auther Tom
  * @Date 2020-03-25 15:55
  * @描述 累加器
  */
class SessionAggrStatAccumulator  extends AccumulatorV2[String, mutable.HashMap[String, Int]] {

  //保存所有聚合的数据
  private val aggrStatMap:mutable.HashMap[String,Int] = mutable.HashMap[String,Int]()

  override def isZero: Boolean = {
    aggrStatMap.isEmpty
  }

  //这个是重点
  override def copy(): AccumulatorV2[String, mutable.HashMap[String, Int]] = {
    val newAcc = new SessionAggrStatAccumulator
    aggrStatMap.synchronized{
      newAcc.aggrStatMap ++= this.aggrStatMap
    }
    newAcc
  }

  override def reset(): Unit = {
    aggrStatMap.clear()
  }

  //把数据放入map中,出现次数累加
  override def add(key: String): Unit = {
    if (!aggrStatMap.contains(key)){
      //如果不包含,就初始化值为0
      aggrStatMap += (key -> 0)
    }
    aggrStatMap.update(key,aggrStatMap(key)+1)
  }

  //把两个map中的数据,相同的key  值进行累加
  override def merge(other: AccumulatorV2[String, mutable.HashMap[String, Int]]): Unit = {
    other match {
        //首先判断是自己的这个累加器
      case acc:SessionAggrStatAccumulator => {
        acc.aggrStatMap.foldLeft(this.aggrStatMap){
          case (map,(k,v)) =>
            map += (k ->(map.getOrElse(k,0) + v))
        }

//        (this.aggrStatMap /: acc.value){ case (map, (k,v)) => map += ( k -> (v + map.getOrElse(k, 0)) )}
      }
    }
  }

  //返回的值
  override def value: mutable.HashMap[String, Int] = {
    this.aggrStatMap
  }
}
