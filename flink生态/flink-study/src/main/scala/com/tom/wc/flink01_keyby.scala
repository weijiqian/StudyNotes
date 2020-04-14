package com.tom.wc

import org.apache.flink.api.scala.ExecutionEnvironment


/**
  * @Auther Tom
  * @Date 2020-04-12 15:54
  * @描述 keyby
  */
object flink01_keyby {
  def main(args: Array[String]): Unit = {

    val environment: ExecutionEnvironment = ExecutionEnvironment.getExecutionEnvironment
    environment.readFile("ddd")

  }
}

case class User(name:String,age:Int)
