package com.heima.freemarker.test;

import com.heima.freemarker.FreeMarkerApplication;
import com.heima.freemarker.entity.Student;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * @author fzj
 * @date 2023-08-14 11:14
 */
@SpringBootTest(classes = FreeMarkerApplication.class)
@RunWith(SpringRunner.class)
public class FreeMarkerTest {
    @Autowired
    private Configuration configuration;

    @Test
    public void test() throws IOException, TemplateException {
        Template template = configuration.getTemplate("02-list.ftl");
        //第一个参数：模型数据，第二个参数；输出流
        template.process(getData(),new FileWriter("d:/list.html"));
    }

    private Map getData(){

        Map<String,Object> map=new HashMap<>();
        Student stu1 = new Student("小强",18,new Date(),77777f);

        //小红对象模型数据
        Student stu2 = new Student("小红",19,new Date(),444444f);

        //将两个对象模型数据存放到List集合中
        List<Student> stus = new ArrayList<>();
        stus.add(stu1);
        stus.add(stu2);

        //向model中存放List集合数据
        map.put("stus",stus);

        //------------------------------------

        //创建Map数据
        HashMap<String,Student> stuMap = new HashMap<>();
        stuMap.put("stu1",stu1);
        stuMap.put("stu2",stu2);
        // 3.1 向model中存放Map数据

        map.put("stuMap",stuMap);

        return map;

    }
}
