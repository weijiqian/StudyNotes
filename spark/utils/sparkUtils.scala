package com.learning.bigdata.utils

import java.io.{FileInputStream, InputStream}
import java.text.SimpleDateFormat
import java.util.{Calendar, Properties}

import org.apache.commons.lang3.StringUtils
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client.{ConnectionFactory, Put, Result}
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.spark.rdd.RDD
//import org.apache.hadoop.hbase.mapred.TableOutputFormat 这个主要针对读取hbase中的老的API的使用
import org.apache.hadoop.hbase.mapreduce.{TableInputFormat, TableOutputFormat}
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.mapred.JobConf
import org.apache.spark.SparkContext
import org.apache.hadoop.mapreduce.Job
import org.apache.spark.sql.SparkSession

object Utiles {

  /**
    * 用正则表达式来过滤一条一条的String类型的数据
    *
    * @param log
    */
  def parseLog(log: String): Unit = {
    if (StringUtils.isNotEmpty(log)) {
      val PATTERN ="""(\S+) (\S+) (\S+) ([.]) (".") (\d{3}) (\d+) (".?") (".*?")""".r

      val logs = PATTERN.findFirstMatchIn(log)
    }
  }

  /** 解析原始数据里的时间 ：[12/Sep/2018:23:33:53 +0800]
    * 获取日期 格式2018-09-27 13:39:35
    * 参数s : 当天为0，前一天为-1
    *
    * @param s
    * @return
    */
  def getDate(s: Int): String = {
    val cal = Calendar.getInstance()
    val sdf = new SimpleDateFormat(
    “ yyyy - MM - dd HH: mm: ss”)
    cal.add(Calendar.DATE, s)
    val day = sdf.format(cal.getTimeInMillis)
    day
  }

  /**
    * 初始化一个SparkSession
    *
    * @return
    */
  def initSparkSession(): SparkSession = {
    SparkSession.builder()
      .appName(Constants.SPARK_NAME)
      .master(Constants.SPARK_MODE)
      .config("spark.sql.warehouse.dir", "/ spark -warehouse /”)
    .config("spark.worker.ui.retainedExecutors”, “ 200”) //减少保存在Worker内存中的Driver,Executor信息
    .config("spark.worker.ui.retainedDrivers”, “ 200”)
    .config("spark.serializer”, “org.apache.spark.serializer.KryoSerializer”)
    .config("spark.kryoserializer.buffer”, “1024m”)
    .config("spark.kryoserializer.buffer.max”, “2046m”)
    .config("spark.io.compression.codec”, “ snappy”)
    .config("spark.sql.parquet.binaryAsString”, “ true”)
    .config("spark.sql.crossJoin.enabled”, “ true”)
    .config("spark.sql.codegen”, “ true”)
    .config("spark.sql.unsafe.enabled”, “ true”)
    .config(“spark.sql.shuffle.partitions”,“200”) //多表查询(比如多表join)，涉及到shuffle的操作会开启200个task来处理数据,由参数控制，可以人为得调大调小
    .config("spark.shuffle.manager”, “ tungsten - sort”)
    .config("spark.network.timeout”, “ 600”)
    .config("spark.testing.memory”, “ 471859200”)
    .getOrCreate()

  }

  /**
    * spark的新API读取hbase里面的数据
    *
    * @param
    */
  def readToHbase(): Unit = {

    val sc = initSparkSession().sparkContext
    val tablename = Constants.TABLE
    //获取hbase的配置
    val conf = HBaseConfiguration.create()
    conf.set(Constants.QUORUM, Constants.HBASEPORT)
    //这里是input
    conf.set(TableInputFormat.INPUT_TABLE, tablename)


    //通过newAPIHadoopRDD方法读取hbase表转换为RDD
    val hbaseRDD = sc.newAPIHadoopRDD(conf, classOf[TableInputFormat], classOf[ImmutableBytesWritable], classOf[Result])

    //通过读取出来的RDD对hbase里面的数据做操作

    //统计hbase里面的数据条数并打印，做数据校验时用
    val hbaseCount = hbaseRDD.count()
    System.err.println(s"表中的数据条数：${hbaseCount}")

    //拿到表中的rowkey    列name     列age

    //循环每一行HBase的数据
    hbaseRDD.foreach { case (_, result) =>
      //获取行键
      val key = Bytes.toString(result.getRow)
      //通过列族和列名获取列  注意：获取列的时候，要用String类型，
      val name = Bytes.toString(result.getValue("cf".getBytes, "name".getBytes))
      val age = Bytes.toString(result.getValue("cf".getBytes, "age".getBytes))
      println("Row key:" + key + " ,name:" + name + ",age :" + age)
    }
    //Row key: row1,name:zs,age:23

    initSparkSession().stop()

  }

  /**
    * spark以RDD使用“旧”的API将数据导入hbase
    *
    * @param
    */
  def writeToHbase(): Unit = {
    // 这里为了提高性能，每一万条入一次HBase库 数据的批量导入 而这里我们是测试用的是一条条的数据导入，实际生产中是批量导入(所以需要写循环)
    val sc: SparkContext = initSparkSession().sparkContext

    val tablename = "account"
    val conf = HBaseConfiguration.create()
    conf.set("hbase.zookeeper.quorum", "cdh1:2181,cdh2:2181,cdh3:2181")
    val conn = ConnectionFactory.createConnection(conf)
    println("打印连接", conn)

    val jobConf = new JobConf(conf)
    jobConf.setOutputFormat(classOf[org.apache.hadoop.hbase.mapred.TableOutputFormat]) //mapred：这个是老的API
    jobConf.set(TableOutputFormat.OUTPUT_TABLE, tablename)

    val indataRDD: RDD[String] = sc.makeRDD(Array("1,jack,15", "2,Lily,16", "3,mike,16"))
    val rdd = indataRDD.map(_.split(',')).map { arr => {
      /*一个Put对象就是一行记录，在构造方法中指定主键
      * 所有插入的数据必须用org.apache.hadoop.hbase.util.Bytes.toBytes方法转换
      * Put.add方法接收三个参数：列族，列名，数据  注意Put.add方法是过时的方法可以用Put.addColumn方法
      */
      //注意：hbase现在只支持String类型数据的存取  hbase现在只支持String类型数据的存取
      val put = new Put(Bytes.toBytes(arr(0).toString))
      put.addColumn(Bytes.toBytes("cf"), Bytes.toBytes("name"), Bytes.toBytes(arr(1)))
      put.addColumn(Bytes.toBytes("cf"), Bytes.toBytes("age"), Bytes.toBytes(arr(2).toString))
      //转化成RDD[(ImmutableBytesWritable,Put)]类型才能调用saveAsHadoopDataset
      (new ImmutableBytesWritable, put)
    }
    }

    rdd.saveAsHadoopDataset(jobConf)
    println("数据导入成功")
    initSparkSession().stop()

  }

  /**
    * spark以RDD使用“新”的API将数据导入hbase
    *
    * @param rdd
    */
  def newWriteToHbase(rdd: RDD[String]): Unit = {
    // 这里为了提高性能，需要每一万条入一次HBase库 数据的批量导入
    //而这里我们是测试没有批量导入，一条一条导入，一条一条导入数据，那么每次就会去访问hbase server
    //插入一条数据 就会将这个插入的操作 提交hbase的server 插入效率很低的 实际生产中需要批量数据导入 一般不用插入单挑数据这种方式

    val sc = initSparkSession().sparkContext
    val applicationId = sc.applicationId
    //要插入的hbase里面的表名
    val tablename = Constants.TABLE
    //设置zooKeeper集群地址，也可以通过将hbase-site.xml导入classpath，但是建议在程序里这样设置
    val conf = HBaseConfiguration.create()
    conf.set(Constants.QUORUM, Constants.HBASEPORT)

    //hbase的连接  用来打印日志定位问题的
    val conn = ConnectionFactory.createConnection(conf)
    System.err.println("hbase连接状态:" + conn + "连接成功")
    //注意这里是output
    conf.set(TableOutputFormat.OUTPUT_TABLE, tablename)

    //创建Job
    val job = Job.getInstance(conf)
    //设置输出的KeyClass
    job.setOutputKeyClass(classOf[ImmutableBytesWritable])
    //设置输出ValueClass
    job.setOutputValueClass(classOf[Result])
    //设置OutputFormat
    job.setOutputFormatClass(classOf[TableOutputFormat[ImmutableBytesWritable]])

    //获取JobConf
    val jobConf = job.getConfiguration()

    //RDD转换成可以进行HBase表数据写入的格式的RDD
    val resultRDD = rdd.map(_.split(",")).map(arr => {
      /*一个Put对象就是一行记录，在构造方法中指定主键
      * 所有插入的数据必须用org.apache.hadoop.hbase.util.Bytes.toBytes方法转换
      * Put.add方法接收三个参数：列族，列名，数据  注意Put.add方法是过时的方法可以用Put.addColumn方法
      */
      //注意：hbase现在只支持String类型数据的存取
      val put = new Put(Bytes.toBytes(arr(0).toString))
      put.addColumn(Bytes.toBytes("cf"), Bytes.toBytes("name"), Bytes.toBytes(arr(1)))
      put.addColumn(Bytes.toBytes("cf"), Bytes.toBytes("age"), Bytes.toBytes(arr(2).toString))
      (new ImmutableBytesWritable, put)
    })

    resultRDD.saveAsNewAPIHadoopDataset(jobConf)
    System.err.println(s"applicationID:${applicationId}数据入库成功")
    sc.stop()

  }

  /**
    * 加载配置文件
    *
    * @param propFile
    * @return
    */
  def loadCfg(propFile: String) = {
    //加载配置文件，约定为bigdata.properties

    val props = new Properties()
    val in: InputStream = this.getClass.getClassLoader().getResourceAsStream("bigdata.properties")

    //启动时driver classpath设定
    if (StringUtils.isBlank(propFile)) {
      if (in != null) {
        props.load(in)
      }
      else {
        println(s"======bigdata.properties load fail")
      }
    } else {
      props.load(new FileInputStream(propFile))
    }

    /* //库名
    val database = cfgContext.getString(PropertiesConstants.database)
    if (database == null) {
    //tableBasePath is required
    println(“properties database is required”)
    System.exit(1)
    }*/

    //表名
    val table = props.getProperty(PropertiesConstants.table)
    if (table == null) {
      println("properties table is required")
      System.exit(1)
    }
    println(s"======loading properties tablename is ${table} ")

    //日志基础路径，如果没此参数
    var logHdfsPath = props.getProperty(PropertiesConstants.logHdfsPath)
    if (logHdfsPath == null) {
      println(s"properties logHdfsPath is required")
      System.exit(1)
    }

    (logHdfsPath, table)


  }

  def main(args: Array[String]): Unit = {

    val sc = initSparkSession().sparkContext
    println(getDate(0))

    val dt = "12/Sep/2018:23:33:53 +0800"
    //val  dt ="19/Dec/2017:01:00:03 +0800"
    System.out.println(ParseTimeUtil.fetchDt(dt))

    /*val spark = initSparkSession()
    val JsonDataFrame = spark.read.json("D:\\json.json")
    JsonDataFrame.show(true)

      */
    /
    val indataRDD = sc.makeRDD(Array(
    “ 4
    , zhangsan
    , 15
    ”, “ 5
    , xiongxiong
    , 56
    ”, “ 6
    , mingming
    , 24
    ”) )
    newWriteToHbase(indataRDD) /

    // readToHbase()

  }
}
