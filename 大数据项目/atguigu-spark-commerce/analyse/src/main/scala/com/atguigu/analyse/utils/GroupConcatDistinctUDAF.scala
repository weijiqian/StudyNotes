package com.atguigu.analyse.utils

import org.apache.spark.sql.Row
import org.apache.spark.sql.expressions.{MutableAggregationBuffer, UserDefinedAggregateFunction}
import org.apache.spark.sql.types.{DataType, StringType, StructField, StructType}
/**
  * @Auther Tom
  * @Date 2020-03-27 21:13
  * @描述 用户自定义聚合函数
  *     功能  :  把输入的数据去重,并且拼接起来
  *     输入数据:  1:北京
  *     2:上海
  *     2:上海
  *     2:上海
  *     1:北京
  *     输出数据:1:北京,2:上海
  *
  *     //解读 group_concat_distinct : 因为是按照area和product_id分组,
  *     // 所以会出现这样的数据,华东 组里面有多条上海的记录, 电脑组里面也有多条记录.
  *     // 因此,就要城市去重处理
  */
class GroupConcatDistinctUDAF extends UserDefinedAggregateFunction {

  //输入数据 1:北京
    override def inputSchema: StructType = StructType(StructField("cityInfo", StringType) :: Nil)

    override def bufferSchema: StructType = StructType(StructField("bufferCityInfo", StringType) :: Nil)

    override def dataType: DataType = StringType

    override def deterministic: Boolean = true

    override def initialize(buffer: MutableAggregationBuffer): Unit = {
      buffer(0)= ""
    }

    /**
      * 更新
      * 可以认为是，一个一个地将组内的字段值传递进来
      * 实现拼接的逻辑
      */
    override def update(buffer: MutableAggregationBuffer, input: Row): Unit = {
      // 缓冲中的已经拼接过的城市信息串
      var bufferCityInfo = buffer.getString(0)
      // 刚刚传递进来的某个城市信息
      val cityInfo = input.getString(0)

      // 在这里要实现去重的逻辑
      // 判断：之前没有拼接过某个城市信息，那么这里才可以接下去拼接新的城市信息
      if(!bufferCityInfo.contains(cityInfo)) {
        if("".equals(bufferCityInfo))
          bufferCityInfo += cityInfo
        else {
          // 比如1:北京
          // 1:北京,2:上海
          bufferCityInfo += "," + cityInfo
        }

        buffer.update(0, bufferCityInfo)
      }
    }

    override def merge(buffer1: MutableAggregationBuffer, buffer2: Row): Unit = {
      var bufferCityInfo1 = buffer1.getString(0);
      val bufferCityInfo2 = buffer2.getString(0);

      for(cityInfo <- bufferCityInfo2.split(",")) {
        if(!bufferCityInfo1.contains(cityInfo)) {
          if("".equals(bufferCityInfo1)) {
            bufferCityInfo1 += cityInfo;
          } else {
            bufferCityInfo1 += "," + cityInfo;
          }
        }
      }

      buffer1.update(0, bufferCityInfo1);
    }

    override def evaluate(buffer: Row): Any = {
      buffer.getString(0)
    }


}

