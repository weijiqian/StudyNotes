package sparkcore
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.{HBaseConfiguration, HColumnDescriptor, HTableDescriptor, TableName}
import org.apache.hadoop.hbase.client.{HBaseAdmin, Put, Result}
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapred.TableOutputFormat
import org.apache.hadoop.hbase.mapreduce.TableInputFormat
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.mapred.JobConf

/**
  * @Auther Tom
  * @Date 2020-03-16 20:47
  */

object Spark27_Hbase {

  def main(args: Array[String]): Unit = {
    //创建spark配置信息
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("JdbcRDD")

    //创建SparkContext
    val sc : SparkContext = new SparkContext(sparkConf)

    //构建HBase配置信息
    val conf: Configuration = HBaseConfiguration.create()
    conf.set("hbase.zookeeper.quorum", "hadoop102,hadoop103,hadoop104")
    conf.set(TableInputFormat.INPUT_TABLE, "rddtable")


//    saveData(sc ,conf)
    readData(sc,conf)

    //关闭连接
    sc.stop()
  }

  def saveData(sc:SparkContext,conf:Configuration)={
    //创建HBaseConf
    val conf = HBaseConfiguration.create()
    val jobConf = new JobConf(conf)
    jobConf.setOutputFormat(classOf[TableOutputFormat])
    jobConf.set(TableOutputFormat.OUTPUT_TABLE, "fruit_spark")

    //构建Hbase表描述器
    val fruitTable = TableName.valueOf("fruit_spark")
    val tableDescr = new HTableDescriptor(fruitTable)
    tableDescr.addFamily(new HColumnDescriptor("info".getBytes))

    //创建Hbase表
    val admin = new HBaseAdmin(conf)
    if (admin.tableExists(fruitTable)) {
      admin.disableTable(fruitTable)
      admin.deleteTable(fruitTable)
    }
    admin.createTable(tableDescr)


    //创建一个RDD
    val initialRDD = sc.parallelize(List((1,"apple",11), (2,"banana",12), (3,"pear",13)))

    //定义往Hbase插入数据的方法
    def convert(triple: (Int, String, Int)) = {
      val put = new Put(Bytes.toBytes(triple._1))
      put.addImmutable(Bytes.toBytes("info"), Bytes.toBytes("name"), Bytes.toBytes(triple._2))
      put.addImmutable(Bytes.toBytes("info"), Bytes.toBytes("price"), Bytes.toBytes(triple._3))
      (new ImmutableBytesWritable, put)
    }

    //将RDD内容写到HBase
    val localData = initialRDD.map(convert)

    localData.saveAsHadoopDataset(jobConf)



  }


  def readData(sc : SparkContext,conf: Configuration): Unit = {

      //从HBase读取数据形成RDD
      val hbaseRDD: RDD[(ImmutableBytesWritable, Result)] = sc.newAPIHadoopRDD(
        conf,
        classOf[TableInputFormat],
        classOf[ImmutableBytesWritable],
        classOf[Result])

      val count: Long = hbaseRDD.count()
      println(count)

      //对hbaseRDD进行处理
      hbaseRDD.foreach {
        case (_, result) =>
          val key: String = Bytes.toString(result.getRow)
          val name: String = Bytes.toString(result.getValue(Bytes.toBytes("info"), Bytes.toBytes("name")))
          val color: String = Bytes.toString(result.getValue(Bytes.toBytes("info"), Bytes.toBytes("color")))
          println("RowKey:" + key + ",Name:" + name + ",Color:" + color)
      }
    }

}

