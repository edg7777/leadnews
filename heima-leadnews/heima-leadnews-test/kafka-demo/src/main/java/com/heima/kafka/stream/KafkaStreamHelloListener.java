package com.heima.kafka.stream;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.ValueMapper;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Arrays;

/**
 * @author fzj
 * @date 2023-09-01 11:25
 */
@Configuration
@Slf4j
public class KafkaStreamHelloListener {

    public KStream<String,String> kStream(StreamsBuilder streamsBuilder){
        //创建Kstream对象，同时指定从哪个topic接收消息
        KStream<String, String> stream = streamsBuilder.stream("fzj-topic-input");
        /**
         * 处理消息的value
         */
        stream.flatMapValues(new ValueMapper<String, Iterable<String>>() {
                    @Override
                    public Iterable<String> apply(String value) {
                        String[] valAry = value.split(" ");
                        return Arrays.asList(valAry);
                    }
                }).groupBy((key,value)->value).windowedBy(TimeWindows.of(Duration.ofSeconds(10))).
                count().toStream()
                .map((key,value)->{
                    System.out.println("key:"+key+",value:"+value);
                    return new KeyValue<>(key.key().toString(),value.toString());
                }).to("fzj-topic-out");
        return stream;
    }
}
