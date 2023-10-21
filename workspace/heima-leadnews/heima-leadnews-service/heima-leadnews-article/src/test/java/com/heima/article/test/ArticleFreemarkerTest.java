package com.heima.article.test;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.article.ArticleApplication;
import com.heima.article.mapper.ApArticleContentMapper;
import com.heima.article.service.ApArticleService;
import com.heima.file.service.FileStorageService;
import com.heima.model.article.pojo.ApArticle;
import com.heima.model.article.pojo.ApArticleContent;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author fzj
 * @date 2023-08-14 14:31
 */
@SpringBootTest(classes = ArticleApplication.class)
@RunWith(SpringRunner.class)
public class ArticleFreemarkerTest {
    @Autowired
    ApArticleContentMapper contentMapper;
    @Autowired
    private Configuration configuration;
    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private ApArticleService apArticleService;
    @Test
    public void upload() throws IOException, TemplateException {
        //获取文章内容(通过文章id来查询)
        ApArticleContent apArticleContent = contentMapper.selectOne(Wrappers.<ApArticleContent>lambdaQuery().eq(ApArticleContent::getArticleId, "1302977558807060482L"));
        if(apArticleContent!=null || StringUtils.isNotBlank(apArticleContent.getContent())){
            //通过freemarker生成html文件
            Template template = configuration.getTemplate("article.ftl");
            //数据模型
            Map<String,Object> content=new HashMap<>();
            content.put("content", JSONArray.parseArray(apArticleContent.getContent()));
            //输出流
            StringWriter out=new StringWriter();
            template.process(content,out);
            //把html文件上传到minio中
            InputStream in= new ByteArrayInputStream(out.toString().getBytes());
            String path = fileStorageService.uploadHtmlFile("", apArticleContent.getArticleId() + ".html", in);
            //修改ap_article表，保存字段
            apArticleService.update(Wrappers.<ApArticle>lambdaUpdate().eq(ApArticle::getId,apArticleContent.getArticleId())
                    .set(ApArticle::getStaticUrl,path));
        }
    }
}
