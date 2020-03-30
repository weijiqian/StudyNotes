## 1.maven-compiler-plugin

设置maven编译的jdk版本，maven3默认用jdk1.5，maven2默认用jdk1.3

```xml
<plugin>                                                                                                                                                                                                  
    <groupId>org.apache.maven.plugins</groupId>                                                                                               
    <artifactId>maven-compiler-plugin</artifactId>                                                                                            
    <version>3.1</version>                                                                                                                    
    <configuration>                                                                                                                                          
        <source>1.8</source> <!-- 源代码使用的JDK版本 -->                                                                                             
        <target>1.8</target> <!-- 需要生成的目标class文件的编译版本 -->                                                                                     
        <encoding>UTF-8</encoding><!-- 字符集编码 -->
        <skipTests>true</skipTests><!-- 跳过测试 -->                                                                                          
    </configuration>                                                                                                                          
</plugin>
```



## 2.maven-jar-plugin

```xml
<!-- 打包jar文件时，配置manifest文件，加入lib包的jar依赖 -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-jar-plugin</artifactId>
    <configuration>
        <classesDirectory>target/classes/</classesDirectory>
        <archive>
            <manifest>
                <mainClass>com.alibaba.dubbo.container.Main</mainClass>
                <!-- 打包时 MANIFEST.MF文件不记录的时间戳版本 -->
                <!--自动加载META-INF/spring目录下的所有Spring配置-->
                <useUniqueVersions>false</useUniqueVersions>
                <addClasspath>true</addClasspath>
                <classpathPrefix>lib/</classpathPrefix>
            </manifest>
            <manifestEntries>
                <Class-Path>.</Class-Path>
            </manifestEntries>
        </archive>
    </configuration>
</plugin>
```



## 3. scala插件

```xml
  <build>
        <defaultGoal>compile</defaultGoal>
        <finalName>WordCount</finalName>
        <plugins>
            <plugin>
                <groupId>net.alchim31.maven</groupId>
                <artifactId>scala-maven-plugin</artifactId>
                <version>3.2.2</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>compile</goal>
                            <goal>testCompile</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
```



## 4.打包插件

```xml
<plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-assembly-plugin</artifactId>
                <version>3.0.0</version>
                <configuration>
                    <archive>
                        <manifest>
                            <mainClass>主类全路径</mainClass>
                        </manifest>
                    </archive>
                    <descriptorRefs>
                        <descriptorRef>
                            jar-with-dependencies
                        </descriptorRef>
                    </descriptorRefs>
                </configuration>
                <executions>
                    <execution>
                        <id>make-assesmbly</id>
                        <phase>package</phase>
                        <goals>
                            <goal>single</goal>
                        </goals>
                    </execution>
                </executions>

            </plugin>
```

