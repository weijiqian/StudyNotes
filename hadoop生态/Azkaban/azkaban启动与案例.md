### 1 启动

先启动  executor

```shell
/opt/module/azkaban/executor/bin/azkaban-executor-start.sh
```

再启动web

```shell
/opt/module/azkaban/server/bin/azkaban-web-start.sh
```



打开浏览器

https://服务器IP地址:8443\



###  2 案例

注意

```shell
目前，Azkaban上传的工作流文件只支持xxx.zip文件。
zip应包含xxx.job运行作业所需的文件和任何文件（文件名后缀必须以.job结尾，否则无法识别）。
作业名称在项目中必须是唯一的。
```



#### 2.1 单一job

描述文件

```shell
[root@hadoop102 jobs]$ vim first.job
#first.job
type=command
command=echo 'this is my first job'
```

打包

```shell
[root@hadoop102 jobs]$ zip first.zip first.job 
```

在浏览器中上传zip包.

脚本写在linux中测试,测试通过,复制到windows中来打包.

#### 2.2  多job流程

1）创建有依赖关系的多个job描述

第一个job：start.job
```shell
[root@hadoop102 jobs]$ vim start.job

#start.job

type=command

command=touch /opt/module/kangkang.txt
```
第二个job：step1.job依赖start.job
```shell
[root@hadoop102 jobs]$ vim step1.job

#step1.job
type=command

dependencies=start

command=echo "this is step1 job"
```
第三个job：step2.job依赖start.job
```shell
[root@hadoop102 jobs]$ vim step2.job

#step2.job

type=command

dependencies=start

command=echo "this is step2 job"
```
第四个job：finish.job依赖step1.job和step2.job
```shell
[root@hadoop102 jobs]$ vim finish.job

#finish.job

type=command

dependencies=step1,step2

command=echo "this is finish job"
```
2）将所有job资源文件打到一个zip包中
```shell
[root@hadoop102 jobs]$ zip jobs.zip start.job step1.job step2.job finish.job

updating: start.job (deflated 16%)

 adding: step1.job (deflated 12%)

 adding: step2.job (deflated 12%)

 adding: finish.job (deflated 14%) 
```
3）在azkaban的web管理界面创建工程并上传zip包



#### 2.3 java 操作任务

使用Azkaban调度java程序

1）编写java程序

```java
import java.io.IOException;

 

public class AzkabanTest {

​	public void run() throws IOException {

​    // 根据需求编写具体代码

​		FileOutputStream fos = new FileOutputStream("/opt/module/azkaban/output.txt");

​		fos.write("this is a java progress".getBytes());

​		fos.close();

  }

 

​	public static void main(String[] args) throws IOException {

​		AzkabanTest azkabanTest = new AzkabanTest();

​		azkabanTest.run();

​	}

}
```
2）将java程序打成jar包，创建lib目录，将jar放入lib内
```shell
[root@hadoop102 azkaban]$ mkdir lib

[root@hadoop102 azkaban]$ cd lib/

[root@hadoop102 lib]$ ll

总用量 4

-rw-rw-r--. 1 root root 3355 10月 18 20:55 azkaban-0.0.1-SNAPSHOT.jar
```

3）编写job文件
```shell

[root@hadoop102 jobs]$ vim azkabanJava.job

\#azkabanJava.job

type=javaprocess

java.class=com.root.azkaban.AzkabanTest

classpath=/opt/module/azkaban/lib/*
```

4）将job文件打成zip包
```shell

[root@hadoop102 jobs]$ zip azkabanJava.zip azkabanJava.job 

 adding: azkabanJava.job (deflated 19%)
```

5）通过azkaban的web管理平台创建project并上传job压缩包，启动执行该job

```shell


[root@hadoop102 azkaban]$ pwd

/opt/module/azkaban

[root@hadoop102 azkaban]$ ll

总用量 24

drwxrwxr-x.  2 root root 4096 10月 17 17:14 azkaban-2.5.0

drwxrwxr-x. 10 root root 4096 10月 18 17:17 executor

drwxrwxr-x.  2 root root 4096 10月 18 20:35 jobs

drwxrwxr-x.  2 root root 4096 10月 18 20:54 lib

-rw-rw-r--.  1 root root  23 10月 18 20:55 output

drwxrwxr-x.  9 root root 4096 10月 18 17:17 server

[root@hadoop102 azkaban]$ cat output 

this is a java progress
```