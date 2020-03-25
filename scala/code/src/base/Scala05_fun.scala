package base

/**
  * @Auther Tom
  * @Date 2020-03-18 20:31
  * @描述 函数
  */
object Scala05_fun {

  def main(args: Array[String]): Unit = {

    //单个参数
    var result = sum(2,5)

    //多个参数
    var result2 : Int = sum(1,2,3,5,6,7)

    //匿名函数
    val f1 = (a:Int,b:Int)=> a + b

    //高阶函数  函数作为参数
    printf(f2(f3,2).toString)

    //高阶函数  函数作为返回值
    var result3 = addBy(4)(5)

    //柯里化 TODO
    val str1:String = "Hello, "
    val str2:String = "Scala!"
    println( "str1 + str2 = " +  strcat(str1)(str2) )

    //异常
    try{
      var a2 = 10 /0
    }catch {
      case ex:Exception => printf("捕获了异常")
    }finally {
      printf("yichang ")
    }





  }

  //柯里化
  def strcat(s1: String)(s2: String) = {
    s1 + s2
  }



  //高阶函数  函数作为返回值
  def addBy(n :Int )={
        if (n % 2 == 0){
          (a:Int ) => (n + a)
        }else{
          (a:Int ) => (n * a)
        }

  }


  //高阶函数  函数作为参数
  def f2(f:Int => Int ,a:Int) = f(a)
  def f3(a:Int)={
    a+10
  }

  //多个参数
  def sum(args:Int*):Int={
    var result = 0
    for (e <- args){
      result += e
    }
    result
  }

  //两个参数
  def sum(a:Int,b:Int):Int={
    a+b
  }
}
