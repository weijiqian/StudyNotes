数据本地化等待时长调节的优化

在项目该如何使用？

通过 spark.locality.wait 参数进行设置，默认为3s，6s，10s。

项目中代码展示：

> new SparkConf().set("spark.locality.wait","10");



作者：z小赵
链接：https://www.jianshu.com/p/99ef69adc2b1
来源：简书
著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。