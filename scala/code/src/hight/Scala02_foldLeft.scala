package hight

/**
  * @Auther Tom
  * @Date 2020-03-27 11:12
  * @描述 foldLeft
  *    该函数的功能是从左往右遍历右边操作类型List,而sum对应的是对应的左边的0，
  *
  *    该函数要求返回值类型和左边类型一致。
  */
object Scala02_foldLeft {
  def main(args: Array[String]): Unit = {

    val list:List[Int] = List(1,2,3,4,5)
    val list2 :List[Int] = List(10,20)

    println("==========int.foldLeft(list)===========")
    (0 /: List(1, 2, 3, 4))((sum, i) => {
      println(s"sum=${sum} i=${i}")
      sum
    })
    /**
      * sum=0 i=1
      * sum=0 i=2
      * sum=0 i=3
      * sum=0 i=4
      */


      println("============list.foldLeft(list)===============")
    val result: List[Int] = list.foldLeft(list2) {
      case (a, b) =>
        println(s"sum=${a.toString()} i=${b.toString}")
        a
    }

    /** 每次循环,a不变,b就是list2的每一项
      * sum=List(10, 20) i=1
      * sum=List(10, 20) i=2
      * sum=List(10, 20) i=3
      * sum=List(10, 20) i=4
      * sum=List(10, 20) i=5
      */

    val result2: List[Int] = list.foldLeft(list2) {
      case (list, item) =>
        list :+ item * 2
    }
    println("=======result2===========")
    println(result2)
    //  List(10, 20, 2, 4, 6, 8, 10)

  }
}
