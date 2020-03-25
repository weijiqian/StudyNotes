package base

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
  * @Auther Tom
  * @Date 2020-03-18 22:17
  * @描述 TODO
  */
object Scala03_Array {
  def main(args: Array[String]): Unit = {

    //数组长度为 3
    var z1:Array[String] = new Array[String](3)

    //变长数组
    var b1 = ArrayBuffer[Int]()
    b1.append(1)
    b1.append(3)
    b1.append(4)
    b1.append(6)

    //定长 --> 可变
    val b2: mutable.Buffer[String] = z1.toBuffer
    //可变 --> 定长
    val zzz: Array[Int] = b1.toArray

    //添加数据
    z1(0) = "a"
    z1(1) = "b"
    z1(2) = "c"

    //直接赋值
    var z2 = Array("Runoob", "Baidu", "Google")

    //取数据
    var data1 = z2(1)

    //多维数组
    import Array._
    val array: Array[Array[Int]] = ofDim[Int](3,4)
    var zz:Array[Array[Int]] = ofDim[Int](2,2)
    // 创建矩阵
    for (i <- 0 to 2) {
      for ( j <- 0 to 2) {
        zz(i)(j) = j;
      }
    }
    // 打印二维阵列
    for (i <- 0 to 2) {
      for ( j <- 0 to 2) {
        print(" " + zz(i)(j));
      }
      println();
    }

    //合并数组
    var myList1 = Array(1.9, 2.9, 3.4, 3.5)
    var myList2 = Array(8.9, 7.9, 0.4, 1.5)
    var myList3 =  concat( myList1, myList2)
    // 输出所有数组元素
    for ( x <- myList3 ) {
      println( x )
    }

    //创建区间数组
    var myList4 = range(10, 20, 2) //步长 2   10 12 14 16 18
    var myList8 = range(10,20) //10 11 12 13 14 15 16 17 18 19


  }
}
