package utils

import java.sql.{Connection, DriverManager, PreparedStatement, ResultSet, Statement}
import java.util.concurrent.{BlockingQueue, LinkedBlockingQueue}

import org.apache.logging.log4j.util.PropertiesUtil
/**
  * @Auther Tom
  * @Date 2020-03-28 22:00
  * @描述 Scala自定义数据库连接池及工具类
  */
class DBUtil {
  /** 配置文件
    * driverClass = com.mysql.jdbc.Driver
    * url = jdbc:mysql://localhosst:3306/test?useSSL=false&useUnicode=true&characterEncoding=utf8
    * userName = root
    * password = root
    * poolSize = 10
    *
    * ————————————————
    * 版权声明：本文为CSDN博主「goldlone」的原创文章，遵循 CC 4.0 BY-SA 版权协议，转载请附上原文出处链接及本声明。
    * 原文链接：https://blog.csdn.net/goldlone/article/details/84861660
    */

  private var connection: Connection = _

  private var preparedStatement: PreparedStatement = _

  private var resultSet: ResultSet = _


  /**
    * 执行指定的SQL
    * @param sql 需要执行的sql
    */
  def execute(sql: String): Unit = {
    try {
      this.connection = DBUtil.getConnection
      this.preparedStatement = this.connection.prepareStatement(sql)
      this.preparedStatement.execute()
    } catch {
      case e: Exception => e.printStackTrace()
    } finally {
      DBUtil.returnConnection(this.connection)
      this.connection == null
    }
  }

  /**
    * 添加记录并返回主键
    * @param sql
    * @param objects
    * @return
    */
  def insertForGeneratedKeys(sql: String, objects: Array[Any]): ResultSet = {
    try {
      this.connection = DBUtil.getConnection
      this.preparedStatement = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
      if(objects != null)
        this.setPreparedStatement(objects)

      this.preparedStatement.executeUpdate()
      this.resultSet = preparedStatement.getGeneratedKeys
    } catch {
      case e: Exception => e.printStackTrace()
    } finally {
      DBUtil.returnConnection(this.connection)
      this.connection == null
    }

    this.resultSet
  }

  /**
    * 执行查询
    * @param sql 需要执行的sql
    * @param objects 参数
    * @return
    */
  def executeQuery(sql: String, objects: Array[Any]): ResultSet = {
    try {
      this.connection = DBUtil.getConnection
      this.preparedStatement = this.connection.prepareStatement(sql)
      if(objects != null)
        this.setPreparedStatement(objects)

      this.resultSet = this.preparedStatement.executeQuery()
    } catch {
      case e: Exception => e.printStackTrace()
    } finally {
      DBUtil.returnConnection(this.connection)
      this.connection == null
    }

    this.resultSet
  }

  /**
    * 执行插入/更新操作
    * @param sql
    * @param objects
    * @return 更新的行数，如果-1则出现异常
    */
  def executeInsertOrUpdate(sql: String, objects: Array[Any]): Int = {
    try {
      this.connection = DBUtil.getConnection
      this.preparedStatement = this.connection.prepareStatement(sql)
      if(objects != null)
        this.setPreparedStatement(objects)

      this.preparedStatement.executeUpdate()
    } catch {
      case e: Exception => e.printStackTrace()
    } finally {
      DBUtil.returnConnection(this.connection)
      this.connection == null
    }

    -1
  }

  /**
    * 对于已经初始化完毕的preparedStatement进行参数赋值
    * @param objects
    */
  private def setPreparedStatement(objects: Array[Any]): Unit = {
    var i = 1
    for(obj <- objects) {
      this.preparedStatement.setObject(i, obj)
      i += 1
    }
  }

  /**
    * 存在则更新，不存在则插入
    * @param sqls 0,1,2 查询，更新，插入
    * @param args 0,1,2 查询，更新，插入
    */
  def existsUpdateElseInsert(sqls: List[String], args: List[Array[Any]]): Unit = {
    try {
      if(sqls.length != 3 || args.length != 3)
        return

      // 1. 查询
      val rs = this.executeQuery(sqls.head, args.head)
      if(rs.next()) {
        // 2.1 更新
        if(sqls(1) != null && args(1) != null)
          this.executeInsertOrUpdate(sqls(1), args(1))
      } else {
        // 2.2 插入
        if(sqls(2) != null && args(2) != null)
          this.executeInsertOrUpdate(sqls(2), args(2))
      }
    } catch {
      case e: Exception => e.printStackTrace()
    }
  }

  /**
    * 释放资源
    */
  def close() : Unit = {
    try {
      if(resultSet != null) resultSet.close()
      if(preparedStatement != null) preparedStatement.close()
      //      if(connection != null) connection.close()
    } catch {
      case e: Exception => e.printStackTrace()
    }
  }
}



/**
  * JDBC 连接池
  * 配置信息从 config/db.properties 中读取
  * @author Created by CN on 2018/11/9 17:37
  */
object DBUtil {

  private val propertiesUtil = new PropertiesUtil("config/db.properties")

  // 数据库驱动类
  private val driverClass: String = propertiesUtil.getStringProperty("driverClass")
  // 数据库连接地址
  private val url: String = propertiesUtil.getStringProperty("url")
  // 数据库连接用户名
  private val username: String = propertiesUtil.getStringProperty("userName")
  // 数据库连接密码
  private val password: String = propertiesUtil.getStringProperty("password")

  // 加载数据库驱动
  Class.forName(driverClass)

  // 连接池大小
  val poolSize: Int = propertiesUtil.getStringProperty("poolSize").toInt

  // 连接池 - 同步队列
  private val pool: BlockingQueue[Connection]  = new LinkedBlockingQueue[Connection]()

  /**
    * 初始化连接池
    */
  for(i <- 1 to poolSize) {
    DBUtil.pool.put(DriverManager.getConnection(url, username, password))
  }


  /**
    * 从连接池中获取一个Connection
    * @return
    */
  private def getConnection: Connection = {
    pool.take()
  }


  /**
    * 向连接池归还一个Connection
    * @param conn
    */
  private def returnConnection(conn: Connection): Unit = {
    DBUtil.pool.put(conn)
  }


  /**
    * 启动守护线程释放资源
    */
  def releaseResource() = {
    val thread = new Thread(new CloseRunnable)
    thread.setDaemon(true)
    thread.start()
  }

  /**
    * 关闭连接池连接资源类
    */
  class CloseRunnable extends Runnable{
    override def run(): Unit = {
      while(DBUtil.pool.size > 0) {
        try {
          //          println(s"当前连接池大小: ${DBUtil.pool.size}")
          DBUtil.pool.take().close()
        } catch {
          case e: Exception => e.printStackTrace()
        }
      }
    }
  }
}
//————————————————
//版权声明：本文为CSDN博主「goldlone」的原创文章，遵循 CC 4.0 BY-SA 版权协议，转载请附上原文出处链接及本声明。
//原文链接：https://blog.csdn.net/goldlone/article/details/84861660