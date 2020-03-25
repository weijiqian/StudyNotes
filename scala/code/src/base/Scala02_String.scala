package base

/**
  * @Auther Tom
  * @Date 2020-03-18 22:15
  * @描述 TODO
  */
object Scala02_String {

  def main(args: Array[String]): Unit = {
    var floatVar = 12.456
    var intVar = 2000
    var stringVar = "菜鸟教程!"
    var fs = printf("浮点型变量为 " +
      "%f, 整型变量为 %d, 字符串为 " +
      " %s", floatVar, intVar, stringVar)
    println(fs)

  }
}
