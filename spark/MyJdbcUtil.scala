import com.alibaba.druid.pool.DruidDataSourceFactory
import java.sql.PreparedStatement
import java.util.Properties
import javax.sql.DataSource
​
object JdbcUtil {
  ​
     var dataSource: DataSource = init()
  ​
     def init() = {
           val properties = new Properties()
           val prop = PropertiesUtil.load("config.properties")
    ​
           properties.setProperty("driverClassName", "com.mysql.jdbc.Driver")
           properties.setProperty("url", prop.getProperty("jdbc.url"))
           properties.setProperty("username", prop.getProperty("jdbc.user"))
           properties.setProperty("password", prop.getProperty("jdbc.password"))
           properties.setProperty("maxActive", prop.getProperty("jdbc.datasource.size"))
    ​
           DruidDataSourceFactory.createDataSource(properties)
    ​
       }
  ​
     def executeUpdate(sql: String, params: Array[Any]): Int = { // "insert into xxx values (?,?,?)"
           var rtn = 0
           var pstmt: PreparedStatement = null
           val connection = dataSource.getConnection
           try {
                 connection.setAutoCommit(false)
                 pstmt = connection.prepareStatement(sql)
      ​
                 if (params != null && params.length > 0) {
                       for (i <- 0 until params.length) {
                             pstmt.setObject(i + 1, params(i))
                         }
                   }
                 rtn = pstmt.executeUpdate()
                 connection.commit()
             } catch {
                 case e: Exception => e.printStackTrace
             }
           rtn
       }
  ​
     def executeBatchUpdate(sql: String, paramsList: Iterable[Array[Any]]): Array[Int] = {
           var rtn: Array[Int] = null
           var pstmt: PreparedStatement = null
           val connection = dataSource.getConnection
           try {
                 connection.setAutoCommit(false)
                 pstmt = connection.prepareStatement(sql)
                 for (params <- paramsList) {
                       if (params != null && params.length > 0) {
                             for (i <- 0 until params.length) {
                                   pstmt.setObject(i + 1, params(i))
                               }
                             pstmt.addBatch()
                         }
                   }
                 rtn = pstmt.executeBatch()
                 connection.commit()
             } catch {
                 case e: Exception => e.printStackTrace
             }
           rtn
       }
  ​
     // 测试
     def main(args: Array[String]): Unit = {
    //        JdbcUtil.executeUpdate("insert into table_1 values(?,?,?,?,?)", Array("take100", "100", 100, 200,300))
           JdbcUtil.executeBatchUpdate("insert into table_1 values(?,?,?,?,?)",List(Array("take101", "100", 200, 200,200),Array("take102", "100", 300, 300,300)))
       }
}
