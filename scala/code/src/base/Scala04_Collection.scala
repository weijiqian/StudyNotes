package base

import scala.collection.mutable.ListBuffer

/**
  * @Auther Tom
  * @Date 2020-03-18 22:32
  * @描述 TODO
  */
object Scala04_Collection {

  def main(args: Array[String]): Unit = {
    //Scala List(列表)
    scalaList()

    //可变 list
    scalaListBuffer()

    //Scala Set(集合)
    scalaSet()

    //Scala Map(映射)
    scalaMap()

    //Scala 元组
    scalaTuple()

    //Scala Option
    scalaOption()

    //Scala Iterator（迭代器）
    scalaIterator()
  }


  //Scala List(列表)
  def scalaList()={
    // 字符串列表
    var site: List[String] = List("Runoob", "Google", "Baidu")
    var site2: List[String] = List("a", "b", "c")

    // 整型列表
    val nums: List[Int] = List(1, 2, 3, 4)

    // 空列表
    val empty: List[Nothing] = List()
    // 空列表
    val empty2 = Nil


    // 二维列表
    val dim: List[List[Int]] =
      List(
        List(1, 0, 0),
        List(0, 1, 0),
        List(0, 0, 1)
      )

    println( "第一网站是 : " + site.head )
    println( "最后一个网站是 : " + site.tail )
    println( "查看列表 site 是否为空 : " + site.isEmpty )

    //列表连接
    var fruit = List.concat(site, site2)

    //取值
    val str: String = site(1)

    //增加值
    val x = List(1,2,3,4)
    //以下操作 x都是不变的,重新生成了新的列表
     val y = x :+ 5  //y = List(1,2,3,4,5)
      val y2 = 5 +: x //y = List(5,1,2,3,4)
    val y3 = 5 :: x   //y = List(5,1,2,3,4)

    //移除
    val y4 = x.drop(2)  //y4 = List(3,4)
    val y5 = x.dropRight(2)//y5=List(1,2)

      val y99: Int = x.reduce((a1,a2) => (a1 + a2))
    val y98 = x.reduce(_ + _)

    //filter
    val y6 = x.filter(i => i != 2) //y6 = List(1,3,4)
    val y7 = x.map(i=>i*2) //y7 = List(2,4,6,8)
    val y8 = x.foreach(i => i*2) //y8 = List(2,4,6,8)
    val y9 = x.mkString(",") //y9 = "1,2,3,4"

  }

  /**
    * 可变集合
    */
  def scalaListBuffer()={
    var list:ListBuffer[Int] = ListBuffer[Int](1,2,3,4)

    list.append(8)
    list.remove(2)

  }

  //Scala Set(集合)
  def scalaSet()={
    //特点 : 唯一不重复,不可变
    val x = Set(1,2,3,4)

    //可变set
    val xx = scala.collection.mutable.Set(1,2,3,4)
    xx.add(5)
    xx.remove(2)


  }

  //Scala Map(映射)
  def scalaMap()={
    //定义
    var x:Map[String,String] = Map(
      "name" -> "tom",
      "age" -> "25",
      "class" -> "math"
    )

    //新增
    x += ("tall" ->"178")
    //取值
    val y1 = x("name")//tom
    val y4 = x.get("name")
    //移除
    val y2 = x - ("name") //移除key为name的项


    println( "x 中的键为 : " + x.keys )
    println( "x 中的值为 : " + x.values )
    println( "检测 x 是否为空 : " + x.isEmpty )
    println( "检测 x 是否为空 : " + x.isEmpty )

    //两个map合并 并且去重
    val y:Map[String,String] = Map(
      "name" -> "jack",
      "age" -> "18"
    )
    val xy = x ++ y

    //打印map
    x.keys.foreach(key => {
      printf("key:",key)
      printf("value:",x(key))
    })

    for ((k,v) <- x){
      printf(k + "===" + v)

    }



  }

  //Scala 元组
  def scalaTuple()={
    //可以包含不同类型  最大长度 22
    val x = (1,2.34,"tom","jack")

    //迭代
    x.productIterator.foreach(value => (
      printf("value:",value)
    ))

    //取值
    var y1 = x._1 //1
    var y2 = x._2 //2..34

  }

  //Scala Option
  def scalaOption()={
    val x:Map[String,String] = Map("k1"->"v1")
    val y1:Option[String] = x.get("k1")
    val y2:Option[String] = x.get("k2")

    printf("k1:",y1) // Some(tom)
    printf("k2:",y2) //None

  }

  //Scala Iterator（迭代器）
  def scalaIterator()={
    val x = Iterator("ali","tengxun","meituan")

    while (x.hasNext){
      printf(x.next())
    }

  }


}
