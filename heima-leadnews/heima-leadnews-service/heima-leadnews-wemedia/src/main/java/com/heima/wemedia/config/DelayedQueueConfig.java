package com.heima.wemedia.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fzj
 * @date 2023-09-13 21:45
 */
@Configuration
public class DelayedQueueConfig {
    public static final String NORMAL_QUEUE_NAME = "normal_queue_name";
    public static final String NORMAL_EXCHANGE_NAME = "normal_exchange_name";
    public static final String NORMAL_ROUTING_KEY = "normal_routing_key";
    public static final String DLX_QUEUE_NAME = "dlx_queue_name";
    public static final String DLX_EXCHANGE_NAME = "dlx_exchange_name";
    public static final String DLX_ROUTING_KEY = "dlx_routing_key";

    /**
     * 定义一个死信队列
     * @return
     */
    @Bean
    public Queue dlxQueue(){
        return new Queue(DLX_QUEUE_NAME,true,false,false);
    }

    /**
     * 定义一个死信交换机
     * @return
     */
    @Bean
    public DirectExchange dlxExchange(){
        return new DirectExchange(DLX_EXCHANGE_NAME,true,false);
    }

    /**
     * 绑定死信队列和死信交换机
     * @return
     */
    @Bean
    public Binding dlxBinding() {
        return BindingBuilder.bind(dlxQueue()).to(dlxExchange())
                .with(DLX_ROUTING_KEY);
    }

    /**
     * 普通队列
     * @return
     */
    @Bean
    public Queue normalQueue(){
        Map<String,Object> args=new HashMap<>();
        //设置消息过期时间
//        args.put("x-message-ttl", 1000*10);
        //设置死信交换机
        args.put("x-dead-letter-exchange", DLX_EXCHANGE_NAME);
        //设置死信 routing_key
        args.put("x-dead-letter-routing-key", DLX_ROUTING_KEY);
        return new Queue(NORMAL_QUEUE_NAME,true,false,false,args);
    }

    /**
     * 普通交换机
     * @return
     */
    @Bean
    public DirectExchange normalExchange(){
        return new DirectExchange(NORMAL_EXCHANGE_NAME,true,false);
    }

    /**
     * 绑定普通队列到普通交换机
     * @return
     */
    @Bean
    public Binding normalBinding(){
        return BindingBuilder.bind(normalQueue()).to(normalExchange()).with(NORMAL_ROUTING_KEY);
    }



}
