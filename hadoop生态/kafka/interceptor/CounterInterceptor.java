package com.atguigu.kafka.interceptor;

import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Map;

/**
 * @author liubo
 */
public class CounterInterceptor implements ProducerInterceptor<String, String> {

    private long successNum = 0L;
    private long errorNum = 0L;

    @Override
    public ProducerRecord<String, String> onSend(ProducerRecord<String, String> record) {
        return record;
    }

    @Override
    public void onAcknowledgement(RecordMetadata metadata, Exception exception) {
        if (exception == null) {
            successNum++;
        } else {
            errorNum++;
        }
    }

    @Override
    public void close() {
        System.out.println("successNum=" + successNum);
        System.out.println("errorNum=" + errorNum);

    }

    @Override
    public void configure(Map<String, ?> configs) {

    }
}
