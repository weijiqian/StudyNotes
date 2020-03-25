package base

import scala.collection.immutable

/**
  * @Auther Tom
  * @Date 2020-03-18 19:22
  * @描述 基础变量
  */
object Scala01_base {


  def main(args: Array[String]): Unit = {

    // 1.基本数据类型
    baseinfo()

    // 2.基本运算符
    methon2()

    // 3.流程控制 for
    methon3()

    // 4 流程控制  while
    methon4()




  }


  /**
   * @MethodName: methon4
   * @Param:  * @param
   * @Return: void
   * @Author: Tom
   * @Date:  2020-03-18  20:25
   * @Description: while
  **/
  def methon4()={
    var a = 2
    var b = 5
    while (a < b){
      a = a + 1
      printf(a.toString)
    }

    //break  没有continue 和 break
    var n = 20
    import util.control.Breaks._
    breakable{
      while (n < 30){
        n += 1
        printf(n.toString)
        if (n == 25){
          break()
        }

      }
    }
    printf(n.toString)

  }

  /**
   * @MethodName: methon3
   * @Param:  * @param
   * @Return: void
   * @Author: Tom
   * @Date:  2020-03-18  19:39
   * @Description: 流程控制
  **/
  def methon3()={
    var a:Int = 2
    var b:Int = 3

    // if
    if (a > b){
      printf("a > b")
    }else {
      printf("a < b")
    }

    //for 1
    var list: List[Int]= List(1,2,3,4)
    for (e <- list){
      printf(e.toString)
    }

    //for 2
    for (e <- 1 to 3){
      printf(e.toString)//1  2  3
    }

    // for 3
    for (e <- 1 until 3){
      printf(e.toString)// 1 2
    }

    //for 4
    for (e <- 1 to 3 if e != 2){
      printf(e.toString)//1  3
    }

    // for 5
    for (x <- 1 to 3 ; y = x + 10){
      printf(y.toString) //11 12  13
    }

    // for 6 双重循环
    for(x <- 1 to 3 ;y <- 1 to 3){
      printf(x*y + "")
    }

    //for 7
     val result: immutable.IndexedSeq[Int] = for(i <- 1 to 3 ) yield i*2


  }


  /**
   * @MethodName: methon2
   * @Param:  * @param
   * @Return: void
   * @Author: Tom
   * @Date:  2020-03-18  19:33
   * @Description: 运算符
  **/
  def methon2()={
    var a:Int = 1
    var b:Int = 2
    printf((a += 2) + "") //3
    printf("a==b:"+ (a == b))
    printf("a>b:"+ (a>b))
  }

  /**
   * @MethodName: baseinfo
   * @Param:  * @param
   * @Return: void
   * @Author: Tom
   * @Date:  2020-03-18  19:31
   * @Description: 基本数据类型
  **/
  def baseinfo() = {
    //var  修饰可变量
    var num:Int = 1
    //val  修饰不可变量
    val num2 : Int = 2

    var char:Char = 'a'
    printf("a.toInt=",char.toInt)

    var long:Long = 3
    var double :Double = 4.0
    var b:Boolean = true
    var f:Float = 5.1f

  }


}
