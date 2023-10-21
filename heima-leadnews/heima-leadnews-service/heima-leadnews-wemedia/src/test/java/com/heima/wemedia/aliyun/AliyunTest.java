package com.heima.wemedia.aliyun;

import com.heima.common.aliyun.GreenImageScan;
import com.heima.common.aliyun.GreenTextScan;
import com.heima.wemedia.WemediaApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

/**
 * @author fzj
 * @date 2023-08-22 11:23
 */
@SpringBootTest(classes = WemediaApplication.class)
@RunWith(SpringRunner.class)
public class AliyunTest {
    @Autowired
    private GreenTextScan greenTextScan;

    @Autowired
    private GreenImageScan greenImageScan;

    /**
     * 测试文本内容
     */
    @Test
    public void testScanText() {
        try {
            Map map = greenTextScan.greeTextScan("我是一个好人");
            System.out.println(map);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 测试图片内容
     */
    @Test
    public void testScanImag(){

    }
}
