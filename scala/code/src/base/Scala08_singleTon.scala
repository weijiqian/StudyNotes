package base

/**
  * @Auther Tom
  * @Date 2020-03-20 11:41
  * @描述 单例模式
  */
object Scala08_singleTon extends App {
  private val singleTon: SingleTon = SingleTon.getInstance()

}

class SingleTon private(){
  printf("做初始化操作")
}
object SingleTon {
  private val singleTon:SingleTon = new SingleTon()
  def getInstance(): SingleTon ={
    singleTon
  }
}