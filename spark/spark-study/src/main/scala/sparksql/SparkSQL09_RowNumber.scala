package sparksql

import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.{SparkConf, SparkContext}

/**
  * @Auther Tom
  * @Date 2020-03-28 11:12
  * @描述 开窗函数  RowNumber
  *    可以让我们实现分组取topn的逻辑
  */
object SparkSQL09_RowNumber {
  def main(args: Array[String]): Unit = {
    // 创建SparkConf，集群运行
    val conf = new SparkConf()
      .setAppName("RowNumberWindowFunction").setMaster("local[*]");

    // 创建JavaSparkContext
    val sc = new SparkContext(conf);
    val sparkSession: SparkSession = SparkSession.builder().config(conf).enableHiveSupport().getOrCreate()

    // 创建销售额表，sales表
    sparkSession.sql("DROP TABLE IF EXISTS sales");
    sparkSession.sql("CREATE TABLE IF NOT EXISTS sales ("
      + "product STRING,"
      + "category STRING, "
      + "revenue BIGINT)");

    //加载数据
    sparkSession.sql("LOAD DATA "
      + "LOCAL INPATH '/usr/local/spark-study/resources/sales.txt' "
      + "INTO TABLE sales");

    // 开始编写我们的统计逻辑，使用row_number()开窗函数
    // 先说明一下，row_number()开窗函数的作用
    // 其实，就是给每个分组的数所在，按照其排序顺序，打上一个分组内的行号
    // 比如说，有一个分组date=20151001, 里面有3条数据，1122，1121，1124，
    // 那么对这个分组的每一行使用row_number()开窗函数以后，三行，依次会获得一个组内的行号
    // 行号从1开始递增，比如1122 1， 1121 2， 1124， 3
    val top3SaleDF:DataFrame = sparkSession.sql(""
      + "SELECT product, category,revenue "
      + "FROM ("
      + "SELECT "
      + "product, "
      + "category, "
      + "revenue, "
      // row_number()开窗函数的语法说明
      // 首先可以，在SELECT查询时，使用row_number()函数
      // 其次，row_number()函数后面先跟上OVER关键字
      // 然后括号中，是PARTITION BY，也就是说根据哪个字段进行分组
      // 其次是可以用ORDER BY 进行组内排序
      // 然后row_number()就可以给每个组内的行，一个组内行号
      + "row_number() OVER (PARTITION BY category ORDER BY revenue DESC) rank "
      + "FROM sales "
      + ") tmp_sales "
      + "WHERE rank<=3");
    // 将每组排名前3的数据，保存到一个表中
    sparkSession.sql("DROP TABLE IF EXISTS top3_sales");
    top3SaleDF.createTempView("temp_top3_sales")
    //保存数据到hive
    sparkSession.sql("insert table top3_sales select * from temp_top3_sales; ")

    // 关闭JavaSparkContext
    sparkSession.close();
  }
//  ————————————————
//  版权声明：本文为CSDN博主「独立小桥风满袖」的原创文章，遵循 CC 4.0 BY-SA 版权协议，转载请附上原文出处链接及本声明。
//  原文链接：https://blog.csdn.net/weixin_32265569/article/details/78427180
}
