### 例子

log4j.properties

```
###############################################
# 一下的配置文件都是以log4j.开头
#    一种就是log4j.properties | log4j.xml
# 最最重要就是第一个log4j.rootLogger,指定log4j日志的输出界别(目的地)
# log4j.rootLogger=INFO,stdout,file意思为：
# 日志可以输INFO级别以上的数据，将日志输出到stdout标准控制输出(控制台)，输出到file
#
# 常见的日志输出级别：DEBUG(调试) < INFO(信息) < WARN(警告) < ERROR(错误) < FATAL(致命错误)
#  日志输出的顺序：和日志输出级别一致，即配置为一个级别，输出的内容只能是该级别及其以上级别的信息
#  INFO（输出的包括 INFO WARN ERROR FATAL）
#  ERROR(ERROR FATAL)
#  所以，一般情况下：在开发，测试环境中，日志的级别为DEBUG；在生产环境中，日志级别为INFO
#
#  输出目的地：
#     日志输出的各种各样的目的地，都是通过Appender来进行实现追加的
# 我们在appender中看到的PatternLayout的格式如下：
#
#%m   输出代码中指定的消息
#%n   输出一个回车换行符，Windows平台为“\r\n”，Unix平台为“\n”
#%p   输出优先级，即日志级别：DEBUG，INFO，WARN，ERROR，FATAL
#%r   输出自应用启动到输出该log信息耗费的毫秒数
#%c   输出所属的类目，通常就是所在类的全名
#%t   输出产生该日志事件的线程名
#%d   输出日志时间点的日期或时间，默认格式为ISO8601，也可以在其后指定格式，比如：%d{yyy MMM dd HH:mm:ss , SSS}，输出类似：2002年10月18日  22 ： 10 ： 28 ， 921
#%l   输出日志事件的发生位置，包括类目名、发生的线程，以及在代码中的行数。举例：Testlog4.main(TestLog4.java: 10 ) log4j配置详解 - stone - stonexmx 的博客
# 常见的Appender
# ConsoleAppender
# FileAppender
#
#
###############################################
##系统的日志级别
#log4j.rootLogger=INFO,stdout

##自定义日志的输出级别
log4j.logger.access = INFO, access

log4j.appender.stdout = org.apache.log4j.ConsoleAppender
log4j.appender.stdout.Target = System.out
log4j.appender.stdout.layout=org.apache.log4j.PatternLayout 
log4j.appender.stdout.layout.ConversionPattern=%d{yyyy-MM-dd HH:mm:ss,SSS} [%p] [%t] [%c] - %m%n

### 保存异常信息到单独文件 ###
log4j.appender.access = org.apache.log4j.DailyRollingFileAppender
log4j.appender.access.File =  /home/bigdata/data/bigscreen/data-access.log
log4j.appender.access.Append = true
log4j.appender.access.DatePattern = '.'yyyy-MM-dd
log4j.appender.access.layout = org.apache.log4j.PatternLayout
log4j.appender.access.layout.ConversionPattern = %m%n
```

### java调用

- 1 系统的日志级别
  log4j.rootLogger=INFO,stdout

```java
Logger logger = Logger.getLogger(TestLog4.class.getName());
logger.log("11111111111")
  logger.info("11111111111")
  logger.debug("11111111111")
  logger.error("11111111111")

```

- 2 自定义日志的输出级别
  log4j.logger.access = INFO, access

```
Logger logger = Logger.getLogger("access");
logger.log("aaaaaa")
```





### 例子

- 其中 [ level ] 是日志输出级别：ERROR、WARN、INFO、DEBUG
  ERROR 为严重错误 主要是程序的错误
  WARN 为一般警告，比如session丢失
  INFO 为一般要显示的信息，比如登录登出
  DEBUG 为程序的调试信息

  appenderName是日志输出位置的配置的命名

log4j.appender.appenderName 

```
org.apache.log4j.ConsoleAppender（控制台）
 org.apache.log4j.FileAppender（文件）
 org.apache.log4j.DailyRollingFileAppender（每天产生一个日志文件）
 org.apache.log4j.RollingFileAppender（文件大小到达指定尺寸的时候产生一个新的文件）
 org.apache.log4j.WriterAppender（将日志信息以流格式发送到任意指定的地方）
```

 

appender.appenderName.layout  格式布局,取值如下

```
 org.apache.log4j.HTMLLayout（以HTML表格形式布局）
 org.apache.log4j.PatternLayout（可以灵活地指定布局模式）
 org.apache.log4j.SimpleLayout（包含日志信息的级别和信息字符串）
 org.apache.log4j.TTCCLayout（包含日志产生的时间、线程、类别等等信息）
```



log4j.appender.appenderName.option中option应替换的属性/选项

```
1.ConsoleAppender控制台选项

　　　　Threshold=DEBUG:指定日志消息的输出最低层次。
　　　　ImmediateFlush=true:默认值是true,意味着所有的消息都会被立即输出。
　　　　Target=System.err：默认情况下是：System.out,指定输出控制台

　　2.FileAppender 文件选项

　　　　Threshold=DEBUF:指定日志消息的输出最低层次。
　　　　ImmediateFlush=true:默认值是true,意谓着所有的消息都会被立即输出。
　　　　File=mylog.txt:指定消息输出到mylog.txt文件。
　　　　Append=false:默认值是true,即将消息增加到指定文件中，false指将消息覆盖指定的文件内容。

　　3.RollingFileAppender 每天生成一个文件选项

　　　　Threshold=DEBUG:指定日志消息的输出最低层次。
　　　　ImmediateFlush=true:默认值是true,意谓着所有的消息都会被立即输出。
　　　　File=mylog.txt:指定消息输出到mylog.txt文件。
　　　　Append=false:默认值是true,即将消息增加到指定文件中，false指将消息覆盖指定的文件内容。
　　　　MaxFileSize=100KB: 后缀可以是KB, MB 或者是 GB. 在日志文件到达该大小时，将会自动滚动，即将原来的内容移到mylog.log.1文件。
　　　　MaxBackupIndex=2:指定可以产生的滚动文件的最大数。



```





## 配置示例



```
### 配置根 ###
log4j.rootLogger = debug,console ,fileAppender,dailyRollingFile,ROLLING_FILE,MAIL,DATABASE

### 设置输出sql的级别，其中logger后面的内容全部为jar包中所包含的包名 ###
log4j.logger.org.apache=dubug
log4j.logger.java.sql.Connection=dubug
log4j.logger.java.sql.Statement=dubug
log4j.logger.java.sql.PreparedStatement=dubug
log4j.logger.java.sql.ResultSet=dubug

### 配置输出到控制台 ###
log4j.appender.console = org.apache.log4j.ConsoleAppender
log4j.appender.console.Target = System.out
log4j.appender.console.layout = org.apache.log4j.PatternLayout
log4j.appender.console.layout.ConversionPattern =  %d{ABSOLUTE} %5p %c{ 1 }:%L - %m%n

### 配置输出到文件 ###
log4j.appender.fileAppender = org.apache.log4j.FileAppender
log4j.appender.fileAppender.File = logs/log.log
log4j.appender.fileAppender.Append = true
log4j.appender.fileAppender.Threshold = DEBUG
log4j.appender.fileAppender.layout = org.apache.log4j.PatternLayout
log4j.appender.fileAppender.layout.ConversionPattern = %-d{yyyy-MM-dd HH:mm:ss}  [ %t:%r ] - [ %p ]  %m%n

### 配置输出到文件，并且每天都创建一个文件 ###
log4j.appender.dailyRollingFile = org.apache.log4j.DailyRollingFileAppender
log4j.appender.dailyRollingFile.File = logs/log.log
log4j.appender.dailyRollingFile.Append = true
log4j.appender.dailyRollingFile.Threshold = DEBUG
log4j.appender.dailyRollingFile.layout = org.apache.log4j.PatternLayout
log4j.appender.dailyRollingFile.layout.ConversionPattern = %-d{yyyy-MM-dd HH:mm:ss}  [ %t:%r ] - [ %p ]  %m%n

### 配置输出到文件，且大小到达指定尺寸的时候产生一个新的文件 ###
log4j.appender.ROLLING_FILE=org.apache.log4j.RollingFileAppender 
log4j.appender.ROLLING_FILE.Threshold=ERROR 
log4j.appender.ROLLING_FILE.File=rolling.log 
log4j.appender.ROLLING_FILE.Append=true 
log4j.appender.ROLLING_FILE.MaxFileSize=10KB 
log4j.appender.ROLLING_FILE.MaxBackupIndex=1 
log4j.appender.ROLLING_FILE.layout=org.apache.log4j.PatternLayout 
log4j.appender.ROLLING_FILE.layout.ConversionPattern=[framework] %d - %c -%-4r [%t] %-5p %c %x - %m%n

### 配置输出到邮件 ###
log4j.appender.MAIL=org.apache.log4j.net.SMTPAppender
log4j.appender.MAIL.Threshold=FATAL
log4j.appender.MAIL.BufferSize=10
log4j.appender.MAIL.From=chenyl@yeqiangwei.com
log4j.appender.MAIL.SMTPHost=mail.hollycrm.com
log4j.appender.MAIL.Subject=Log4J Message
log4j.appender.MAIL.To=chenyl@yeqiangwei.com
log4j.appender.MAIL.layout=org.apache.log4j.PatternLayout
log4j.appender.MAIL.layout.ConversionPattern=[framework] %d - %c -%-4r [%t] %-5p %c %x - %m%n

### 配置输出到数据库 ###
log4j.appender.DATABASE=org.apache.log4j.jdbc.JDBCAppender
log4j.appender.DATABASE.URL=jdbc:mysql://localhost:3306/test
log4j.appender.DATABASE.driver=com.mysql.jdbc.Driver
log4j.appender.DATABASE.user=root
log4j.appender.DATABASE.password=
log4j.appender.DATABASE.sql=INSERT INTO LOG4J (Message) VALUES ('[framework] %d - %c -%-4r [%t] %-5p %c %x - %m%n')
log4j.appender.DATABASE.layout=org.apache.log4j.PatternLayout
log4j.appender.DATABASE.layout.ConversionPattern=[framework] %d - %c -%-4r [%t] %-5p %c %x - %m%n
log4j.appender.A1=org.apache.log4j.DailyRollingFileAppender
log4j.appender.A1.File=SampleMessages.log4j
log4j.appender.A1.DatePattern=yyyyMMdd-HH'.log4j'
log4j.appender.A1.layout=org.apache.log4j.xml.XMLLayout
```



### ConversionPattern 日志信息，符号所代表的含义：



```


 -X号: X信息输出时左对齐；
 %p: 输出日志信息优先级，即DEBUG，INFO，WARN，ERROR，FATAL,
 %d: 输出日志时间点的日期或时间，默认格式为ISO8601，也可以在其后指定格式，比如：%d{yyy MMM dd HH:mm:ss,SSS}，输出类似：2002年10月18日 22：10：28，921
 %r: 输出自应用启动到输出该log信息耗费的毫秒数
 %c: 输出日志信息所属的类目，通常就是所在类的全名
 %t: 输出产生该日志事件的线程名
 %l: 输出日志事件的发生位置，相当于%C.%M(%F:%L)的组合,包括类目名、发生的线程，以及在代码中的行数。举例：Testlog4.main (TestLog4.java:10)
 %x: 输出和当前线程相关联的NDC(嵌套诊断环境),尤其用到像java servlets这样的多客户多线程的应用中。
 %%: 输出一个"%"字符
 %F: 输出日志消息产生时所在的文件名称
 %L: 输出代码中的行号
 %m: 输出代码中指定的消息,产生的日志具体信息
 %n: 输出一个回车换行符，Windows平台为"\r\n"，Unix平台为"\n"输出日志信息换行
 可以在%与模式字符之间加上修饰符来控制其最小宽度、最大宽度、和文本的对齐方式。如：
 1)%20c：指定输出category的名称，最小的宽度是20，如果category的名称小于20的话，默认的情况下右对齐。
 2)%-20c:指定输出category的名称，最小的宽度是20，如果category的名称小于20的话，"-"号指定左对齐。
 3)%.30c:指定输出category的名称，最大的宽度是30，如果category的名称大于30的话，就会将左边多出的字符截掉，但小于30的话也不会有空格。
 4)%20.30c:如果category的名称小于20就补空格，并且右对齐，如果其名称长于30字符，就从左边较远输出的字符截掉。
```

