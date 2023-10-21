package com.minio;

import com.heima.file.service.FileStorageService;
import com.heima.minio.MinIOApplication;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * @author fzj
 * @date 2023-08-14 13:21
 */
@SpringBootTest(classes = MinIOApplication.class)
@RunWith(SpringRunner.class)
public class MinIOTest {

//    @Autowired
//    private FileStorageService fileStorageService;
//    @Test
//    public void test(){
//        FileInputStream fileInputStream= null;
//        try {
//            fileInputStream = new FileInputStream("D:\\list.html");
//        } catch (FileNotFoundException e) {
//            throw new RuntimeException(e);
//        }
//        String path = fileStorageService.uploadHtmlFile("", "list.html", fileInputStream);
//        System.out.println(path);
//    }
    /**
     * 获取list.html的连接信息，并且可以在浏览器中访问
     */
    @Test
    public void upload() {
        try {
            FileInputStream fileInputStream=new FileInputStream("D:\\BrowserLoad\\js\\index.js");
            //获取minio的链接信息，创建一个minio的客户端
            MinioClient minioClient = MinioClient.builder().credentials("minio", "minio123").endpoint("http://192.168.126.10:9000").build();

            //上传
            PutObjectArgs putObjectArgs= PutObjectArgs.builder()
                    .object("plugins/js/index.js") //文件名称
                    .contentType("text/js") //文件类型
                    .bucket("leadnews") //桶名称
                    .stream(fileInputStream,fileInputStream.available(),-1).build();
            minioClient.putObject(putObjectArgs);
            //访问路径
//            System.out.println("http://192.168.126.10:9000/leadnews/list.html");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
