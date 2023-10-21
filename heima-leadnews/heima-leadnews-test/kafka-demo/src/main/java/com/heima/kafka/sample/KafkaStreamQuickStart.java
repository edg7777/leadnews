package com.heima.kafka.sample;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.ValueMapper;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

/**
 * @author fzj
 * @date 2023-09-01 10:50
 */

public class KafkaStreamQuickStart {

    public static void main(String[] args) {
        //kafka的配置信息
        Properties prop = new Properties();
        prop.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG,"192.168.126.10:9092");
        prop.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        prop.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        prop.put(StreamsConfig.APPLICATION_ID_CONFIG,"streams-quickStart");

        //stream构建器
        StreamsBuilder streamsBuilder = new StreamsBuilder();
        //流式计算
        streamProcessor(streamsBuilder);
        //创建kafkastream 对象
        KafkaStreams kafkaStreams=new KafkaStreams(streamsBuilder.build(),prop);
        //开启流式计算
        kafkaStreams.start();
    }

    /**
     * 流式计算
     * 消息的内容：hello kafka
     * @param streamsBuilder
     */
    private static void streamProcessor(StreamsBuilder streamsBuilder) {
        //创建Kstream对象，同时指定从哪个topic接收消息
        KStream<String, String> stream = streamsBuilder.stream("fzj-topic-input");
        /**
         * 处理消息的value
         */
        stream.flatMapValues(new ValueMapper<String, Iterable<?>>() {
            @Override
            public Iterable<String> apply(String value) {
                String[] valAry = value.split(" ");
                return Arrays.asList(valAry);
            }
        }).groupBy((key,value)->value).windowedBy(TimeWindows.of(Duration.ofSeconds(10))).
                                        count().toStream()
                                        .map((key,value)->{
                                            System.out.println("key:"+key+",value:"+value);
                                            return new KeyValue<>(key.key().toString(),value);
                                        }).to("fzj-topic-out");
    }
}
