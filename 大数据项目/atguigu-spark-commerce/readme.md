## 概述

电商分析平台是对用户访问电商平台的行为进行分析



## 技术要点:

1. flume
2. kafka
3. spark  core
4. spark sql
5. spark streaming

## 流程图





![image-20200324111030545](/Users/weijiqian/Desktop/大数据/StudyNotes/image-md/image-20200324111030545.png)





## 项目结构说明

#### common 模块

​		各种工具类;



#### mock 模块

​			生成数据类

​			

#### analyse  分析模块





## 需求概述

### 1. 用户session分析

<img src="/Users/weijiqian/Desktop/大数据/StudyNotes/image-md/2020032401.png" />



数据源在spark-warehouse中

1. 需求一:Session 各范围访问步长、访问时长占比统计
2. 需求二:Session 随机抽取
3. 需求三: Top10 热门品类
4. 需求四: Top10 热门品类 Top10 活跃 Session 统计

使用的技术是sparkcore



### 2 需求五:页面转化率统计

​	5. 使用的技术是sparkcore

  

### 3 需求六:各区域 Top3 商品统计

​	6. 使用的技术是sparksql,Dataframe的运用



### 4 广告业务

​	7.广告黑名单实时统计

​	8.广告点击量实时统计

​	9.各省热门广告实时统计

​	10.最近一小时广告点击量实时统计

使用的技术

