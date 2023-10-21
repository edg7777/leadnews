package com.heima.kafka.sample;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

/**
 * @author fzj
 * @date 2023-08-28 22:49
 */
public class ProducerQuickStart {
    public static void main(String[] args) {
        //kafka链接配置信息
        Properties prop=new Properties();
        //kafka的连接地址
        prop.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"192.168.126.10:9092");
        //key和value的序列化
        prop.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringSerializer");
        prop.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringSerializer");
        //ack配置，消息确认机制
        prop.put(ProducerConfig.ACKS_CONFIG,"all");
        //数据压缩
        prop.put(ProducerConfig.COMPRESSION_TYPE_CONFIG,"snappy");
        //创建kafka生产者对象
        KafkaProducer<String, String> producer = new KafkaProducer<>(prop);
        //发送消息
        //第一个参数：topic 第二个参数：消息的key  第三个参数：消息的value
        for (int i = 0; i <5 ; i++) {
            ProducerRecord<String,String> kvProducerRecord =new ProducerRecord<>("fzj-topic-input","hello kafka") ;
            producer.send(kvProducerRecord);
        }

        //关闭消息通道 必须关闭否则消息发送不成功
        producer.close();
    }
}
