package com.pinyougou.upload.controller;

import com.pinyougou.common.utils.FastDFSClient;
import entity.Result;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @ClassName:UploadFileController
 * @Author：Mr.lee
 * @DATE：2019/06/28
 * @TIME： 9:18
 * @Description: TODO
 */

@RestController  //带jsonde 扫描注解
@RequestMapping("/upload")
public class UploadFileController {


    //文件上传的后台方法
    @RequestMapping("/uploadFile")
    //支持跨域 只有这个两个的跨域请求上传图片才可以被允许
    @CrossOrigin(origins = {"http://localhost:9101","http://localhost:9102"},allowCredentials = "true")
    public Result uploadFile(@RequestParam MultipartFile file){

        try {
            //1.创建配置文件配置文件

            //2.获取字节码
            byte[] bytes = file.getBytes();

            //3.获取扩展名,扩张名需要不带.的。.jsp   12345.jsp
            String filename = file.getOriginalFilename();
            //字符串截取，截取到不带点的  jsp
            String extName = filename.substring(filename.lastIndexOf(".")+1);


            //4.核心的代码，文件的上传，使用文件上传工具类,文件上传的配置类路径，不要写死
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:config/fastdfs_client.conf");

            //文件上传接收文件的路径，文件要进行显示，还需拼接上http://192.168.25.133
            String file_id = fastDFSClient.uploadFile(bytes, extName);

            //拼接URL
            String readUrl = "http://192.168.25.129/"+file_id;

            return new Result(true,readUrl);

        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"上传失败");
        }

    }
}
