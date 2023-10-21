package com.heima.freemarker.controller;

import com.heima.freemarker.entity.Student;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author fzj
 * @date 2023-08-14 10:48
 */
@Controller
public class FreeMarkerController {

    @GetMapping("/begin")
    public String begin(Model model){
        model.addAttribute("name","freemarker");
        Student stu = new Student("fzj", 18, new Date(), 5000F);
        model.addAttribute("stu",stu);
        return "01-basic";
    }

    @GetMapping("/list")
    public String list(Model model){
        Student stu1 = new Student("小强",18,new Date(),77777f);

        //小红对象模型数据
        Student stu2 = new Student("小红",19,new Date(),444444f);

        //将两个对象模型数据存放到List集合中
        List<Student> stus = new ArrayList<>();
        stus.add(stu1);
        stus.add(stu2);

        //向model中存放List集合数据
        model.addAttribute("stus",stus);

        //------------------------------------

        //创建Map数据
        HashMap<String,Student> stuMap = new HashMap<>();
        stuMap.put("stu1",stu1);
        stuMap.put("stu2",stu2);
        // 3.1 向model中存放Map数据
        model.addAttribute("stuMap", stuMap);

        return "02-list";
    }
}
