package base

import scala.beans.BeanProperty

/**
  * @Auther Tom
  * @Date 2020-03-20 10:33
  * @描述 类
  */
object Scala06_Class {
  def main(args: Array[String]): Unit = {
    var p1:Point = new Point()
    var p2:Point = new Point(3,6)

    p1.setX(5)
    p1.setY(8)
    p1.move(2,4)
    p2.move(4,6)

    var l1 :Location = new Location()
    var l3 :Location = new Location(4,6,9)
    l1.move(2,4)
    l3.move(5,8,9)


  }
}

//定义
class Point {
  @BeanProperty
  var x:Int = _

  //这个属性自动生成get  set 方法
  @BeanProperty
  var y:Int = _

  def this(x:Int){
    this()//这个是必须的
    this.x = x
  }

  def this(x:Int,y:Int){
    this()
    this.x = x
    this.y = y
  }



  def move(dx:Int,dy:Int): Unit ={
    x = x + dx
    y = y + dy
    printf("x的坐标:"+ x)
    printf("y的坐标:"+ y)
  }
}

//继承
class Location extends Point{

  var z:Int = _

  def this(x:Int,y:Int,z:Int){
    this()
    this.x = x
    this.y = y
    this.z = z
  }



  def move(dx: Int, dy: Int ,dz :Int): Unit = {
    x += dx
    y += dy
    z += dz
    printf("这里是location的move方法")
  }
}
