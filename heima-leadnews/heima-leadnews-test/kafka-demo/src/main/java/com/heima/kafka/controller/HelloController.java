package com.heima.kafka.controller;

import com.alibaba.fastjson.JSON;
import com.heima.kafka.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author fzj
 * @date 2023-08-29 20:32
 */
@RestController
public class HelloController {
    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;

    @GetMapping("/hello")
    public String hello(){
//        kafkaTemplate.send("fzj-topic","冯子杰");
        User user = new User("冯子杰", 19);
        kafkaTemplate.send("user-topic", JSON.toJSONString(user));
        return "ok";

    }
}
