package base

/**
  * @Auther Tom
  * @Date 2020-03-26 15:17
  * @描述 map
  *    flatten  就是将多个集合展开，组合成新的一个集合
  *    flatmap = map + flatten
  *    什么时候使用flatmap  当map的结果是嵌套集合时,可以用flatmap转为单层集合
  */
object Scala12_map_flatmap {
  def main(args: Array[String]): Unit = {

    //单元素
    val data1 = Array(1,2,3,4,5)
    val resultmap1: Array[Int] = data1.map(item => item*2)
    data1.foreach(item => println(item*3))
    //报错
    //data1.flatMap(item => item *3)

    val data2 = List(List(1,2,3),List(4,5,6),List(3,6,9,12))
    data2.flatMap { aaaa =>
      aaaa.map(item => item*2)
    }


    val data3 = Array("a_b","c_d","e_f")
    val resultFlatmap3: Array[String] = data3.flatMap(item => item.split("_"))
    val resultMap3: Array[Array[String]] = data3.map(item => item.split("_"))
    val resultFlatten3: Array[Char] = resultFlatmap3.flatten

    val flatten: List[Int] = data2.flatten
    /**
      * 遇到像resultMap3 这样两层的结构时,通过flatmap 可以把里面的一层拆出来,变为一层结构
      */
    println("resultFlatmap3:================")
    resultFlatmap3.foreach(println)

    println("resultMap3:================")
    resultMap3.foreach(println)

    println("resultFlatten3:================")
    resultFlatten3.foreach(println)

    println("flatten:================")
    flatten.foreach(println)



  }
}
