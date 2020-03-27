/*
 * Copyright (c) 2018. Atguigu Inc. All Rights Reserved.
 */

package com.atguigu.analyse.utils

/**
  * 广告黑名单
  * @param userid
  */
case class AdBlacklist(userid:Long)

/**
  * 用户广告点击量
  * @author Tom
  *
  */
case class AdUserClickCount(date:String,
                            userid:Long,
                            adid:Long,
                            clickCount:Long)


/**
  * 广告实时统计
  * @author Tom
  *
  */
case class AdStat(date:String,
                  province:String,
                  city:String,
                  adid:Long,
                  clickCount:Long)

/**
  * 各省top3热门广告
  * @author Tom
  *
  */
case class AdProvinceTop3(date:String,
                          province:String,
                          adid:Long,
                          clickCount:Long)

/**
  * 广告点击趋势
  * @author Tom
  *
  */
case class AdClickTrend(date:String,
                        hour:String,
                        minute:String,
                        adid:Long,
                        clickCount:Long)