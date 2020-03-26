package base

import scala.math.Ordering

/**
  * @Auther Tom
  * @Date 2020-03-26 22:09
  * @描述 排序
  */
object Scala13_sort {



  /**
    * 1）sorted
    *
    * 对一个集合进行自然排序，通过传递隐式的Ordering
    *
    * （2）sortBy
    *
    * 对一个属性或多个属性进行排序，通过它的类型。
    *
    * （3）sortWith
    *
    * 基于函数的排序，通过一个comparator函数，实现自定义排序的逻辑。
    */

  def main(args: Array[String]): Unit = {

      //  例子 1 ：基于单集合单字段的排序
      example1()

    //例子 2  基于元组多字段的排序
    example2()

    //例子 3  基于类的排序
    exampl3()



  }

  /**
    * 例子 1 ：基于单集合单字段的排序
    */
  def example1() = {

    val list = List(1,5,2,7,4,9)
    println("==============sorted排序=================")
    println(list.sorted) //升序
    println(list.sorted.reverse) //降序
    println("==============sortBy排序=================")
    println( list.sortBy(d=>d) ) //升序
    println( list.sortBy(d=>d).reverse ) //降序
    println("==============sortWith排序=================")
    println( list.sortWith(_<_) )//升序
    println( list.sortWith(_>_) )//降序

    //结果
    //==============sorted排序=================
    //List(1, 2, 4, 5, 7, 9)
    //List(9, 7, 5, 4, 2, 1)
    //==============sortBy排序=================
    //List(1, 2, 4, 5, 7, 9)
    //List(9, 7, 5, 4, 2, 1)
    //==============sortWith排序=================
    //List(1, 2, 4, 5, 7, 9)
    //List(9, 7, 5, 4, 2, 1)

  }

  /**
    * 基于元组多字段的排序
    * 基于sortBy的实现比较优雅，语义比较清晰，sortWith灵活性更强，但代码稍加繁琐
    */
  def example2()={
    //注意多字段的排序，使用sorted比较麻烦，这里给出使用sortBy和sortWith的例子

    //先看基于sortBy的实现：
    val pairs = Array(
      ("a", 5, 1),
      ("c", 3, 1),
      ("b", 1, 3)
    )

    //按第三个字段升序，第一个字段降序，注意，排序的字段必须和后面的tuple对应
    val bx= pairs.
      sortBy(r => (r._3, r._1))( Ordering.Tuple2(Ordering.Int, Ordering.String.reverse) )
    //按照 第二个字段降序
    val bx2 = pairs.sortBy(r => r._2)(Ordering.Int.reverse)
    //打印结果
    println("===============bx  sort ")
    bx.foreach( println )
    /** 结果:
      * (c,3,1)
      * (a,5,1)
      * (b,1,3)
      */

    println("===============bx2  sort ")
    bx2.foreach( println )
    /** 结果:
      * (a,5,1)
      * (c,3,1)
      * (b,1,3)
      */

    //再看基于sortWith的实现：
    val bx3= pairs.sortWith{
      case (a,b)=>{
        if(a._3==b._3) {//如果第三个字段相等，就按第一个字段降序
          a._1>b._1
        }else{
          a._3<b._3 //否则第三个字段升序
        }
      }
    }
    //打印结果
    println("===============bx3  sortwith ")
    bx3.foreach(println)
    /** 结果
      * (c,3,1)
      * (a,5,1)
      * (b,1,3)
      */

  }

  /**
    * 例子三：基于类的排序
    */
  def exampl3()={

    val p1=Person("cat",23)
    val p2=Person("dog",23)
    val p3=Person("andy",25)

    val pairs = Array(p1,p2,p3)

    //sortby
    //先按年龄排序，如果一样，就按照名称降序排
    val bx= pairs.sortBy(person => (person.age, person.name))( Ordering.Tuple2(Ordering.Int, Ordering.String.reverse) )

    bx.foreach(println)
    /**
      * Person(dog,23)
      * Person(cat,23)
      * Person(andy,25)
      */

    //sortWith
    val bx2=pairs.sortWith{
      case (person1,person2)=>{
        person1.age==person2.age match {
          case true=> person1.name>person2.name //年龄一样，按名字降序排
          case false=>person1.age<person2.age //否则按年龄升序排
        }
      }
    }
    bx2.foreach(println)

    /**
      * Person(dog,23)
      * Person(cat,23)
      * Person(andy,25)
      */

  }

}

case class Person(val name:String,val age:Int)
//————————————————
//    版权声明：本文为CSDN博主「三劫散仙」的原创文章，遵循 CC 4.0 BY-SA 版权协议，转载请附上原文出处链接及本声明。
//    原文链接：https://blog.csdn.net/u010454030/article/details/79016996