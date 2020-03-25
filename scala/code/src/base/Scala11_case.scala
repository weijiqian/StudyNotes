package base

/**
  * @Auther Tom
  * @Date 2020-03-20 14:11
  * @描述 case
  */
object Scala11_case extends App {

  //1 .数值匹配
  var oper:String = "#"
  var n1 = 10
  var n2 = 20
  var res = 0

  //不用加 break  自动中断
  oper match {
    case "+" => res = n1 + n2
    case "-" => res = n1 - n2
    case "*" => res = n1 * n2
    case "/" => res = n1 / n2
    case str => {
      //str  就是 oper的值
      printf("这是受到的值:" + str)
    }
    case _ => res = 100  //都没有匹配到的默认值  如果不写,就会报错
  }


  // 2 类型匹配
  // 类型匹配, obj 可能有如下的类型
  val a = 7
  val obj = if(a == 1) 1
  else if(a == 2) "2"
  else if(a == 3) BigInt(3)
  else if(a == 4) Map("aa" -> 1)
  else if(a == 5) Map(1 -> "aa")
  else if(a == 6) Array(1, 2, 3)
  else if(a == 7) Array("aa", 1)
  else if(a == 8) Array("aa")


  val result = obj match {
    case a : Int => a
    case b : scala.collection.immutable.Map[String, Int] => "对象是一个字符串-数字的Map集合"
    case c : scala.collection.immutable.Map[Int, String] => "对象是一个数字-字符串的Map集合"
    case d : Array[String] => "对象是一个字符串数组"
    case e : Array[Int] => "对象是一个数字数组"
    case f : BigInt => Int.MaxValue
    case _ => "啥也不是"
  }
  println(result)

  //3. 匹配数组   列表,元组同理
  for (arr <- Array(Array(0), Array(1, 0), Array(0, 1, 0),
    Array(1, 1, 0), Array(1, 1, 0, 1))) {
    val result = arr match {
      case Array(0) => "0"
      case Array(x, y) => x + "=" + y
      case Array(0, _*) => "以0开头和数组"
      case _ => "什么集合都不是"
    }
    println("result = " + result)
  }

  //4. 变量声明
  var (x,y) = (5,8)

  //5. for循环中
  var map:Map[String,String] = Map("1" -> "a","2"->"b")
  for ((k,v) <- map){
    printf(k +"===" + v)
  }

  //6. 样例类

}
