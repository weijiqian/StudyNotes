package hight

/**
  * @Auther Tom
  * @Date 2020-03-27 11:06
  * @描述 scala 两个map合并，key相同时value相加
  */
object Scala01_TwoMapAdd {

  def main(args: Array[String]): Unit = {


    /**
      * map方法
      */
    val map1 = Map("key1" -> 1, "key2" -> 3, "key3" -> 5)
    val map2 = Map("key2" -> 4, "key3" -> 6, "key5" -> 10)
    val mapAdd1 = map1 ++ map2.map(t => t._1 -> (t._2 + map1.getOrElse(t._1, 0)))
    println(mapAdd1)
    //


    /**
      * 用foldLeft
      */
    val mapAdd2 = map1.foldLeft(map2) {
      case (map, (k, v)) => {
        map + (k -> (v + map.getOrElse(k, 0)))
      }
    }


  }
}

//  ————————————————
//    版权声明：本文为CSDN博主「董可伦」的原创文章，遵循 CC 4.0 BY-SA 版权协议，转载请附上原文出处链接及本声明。
//    原文链接：https://blog.csdn.net/dkl12/article/details/80246048
