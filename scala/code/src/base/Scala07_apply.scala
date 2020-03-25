package base


/**
  * @Auther Tom
  * @Date 2020-03-20 11:07
  * @描述 半生对象
  */
object Scala07_apply {
  def main(args: Array[String]): Unit = {
    var cat:Cat = Cat("tome")
    cat.say()
  }
}

class Cat(){
  var name:String = ""

  def this(name:String){
    this()
    this.name = name
  }

  def say(): Unit ={
    printf("my name id ",name)
  }
}
object Cat{

  //半生对象
  def apply(): Cat = {
    printf("做你想做的操作")
    new Cat()
  }

  def apply(name: String): Cat = new Cat(name)

}
