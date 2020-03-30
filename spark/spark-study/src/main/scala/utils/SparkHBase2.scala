package utils

/**
  * @Auther Tom
  * @Date 2020-03-28 21:58
  * @描述  HBaseTools的使用
  */
object SparkHBase2 {
  def main(args: Array[String]): Unit = {
    val table=HBaseTools.openTable("t_prod_weixin_art");
    val rows=HBaseTools.scanValueDatas(table, "info", "content", 10).toArray()
    rows.foreach(println)
    //关闭资源
    HBaseTools.closeTable(table)
    HBaseTools.closeConn()
  }
}
