package base

/**
  * @Auther Tom
  * @Date 2020-03-20 11:46
  * @描述 特质  相当于接口
  */
object Scala09_trait extends App {


}

trait T1 {
  //无参数
  def sayT1()
}
trait T2{
  //无返回值
  def sayT2(name:String)
}

trait T3 {
  //有返回值
  def sayT3(age:Int):Int
}
class A {
  printf("wo shi a ")
}

//多个 trait 用 with 连接
class B extends A with T1 with T2 with T3{
  override def sayT1(): Unit = {
    printf("来自于 t1")
  }

  override def sayT2(name: String): Unit = {
    printf("我的名字 : ",name)
  }

  override def sayT3(age: Int): Int = {
    printf("我的年龄:" + age)
    age+10
  }
}
