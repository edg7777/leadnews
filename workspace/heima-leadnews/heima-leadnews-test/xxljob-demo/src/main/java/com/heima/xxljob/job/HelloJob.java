package com.heima.xxljob.job;

import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.stereotype.Component;

/**
 * @author fzj
 * @date 2023-08-31 21:03
 */
@Component
public class HelloJob {

    @XxlJob("demoJobHandler")
    public void helloJob(){
        System.out.println("简单任务执行了");
    }
}
