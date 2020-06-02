package com.aura.sink

import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.sink.{RichSinkFunction, SinkFunction}

class MySink2 extends RichSinkFunction[(String, Long)]{

  var parameters: Configuration = _


  override def open(parameters: Configuration): Unit = {

    this.parameters = parameters
  }

  override def close(): Unit = {


  }

  override def invoke(value: (String, Long), context: SinkFunction.Context[_]): Unit = {


  }
}
