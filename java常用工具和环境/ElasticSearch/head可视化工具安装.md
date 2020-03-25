### 两种方法安装

### 1 安装在服务端

- 1 下载  

  ​		git clone git://github.com/mobz/elasticsearch-head.git

- 2   配置    vi elasticsearch/config/elasticsearch.yml  添加内容如下：

```
http.cors.enabled: true
http.cors.allow-origi: "*"
```

- 3 启动(需要node,npm ) 

  ​		到elasticsearch-head目录下
    	下载依赖命令：npm install
   	 启动命令：npm run start
   	 后台启动命令：nohup npm run start &

- 4   浏览器打开

  ​	http://ip+9100/



### 2 谷歌浏览器插件打开