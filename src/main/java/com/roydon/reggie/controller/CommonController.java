package com.roydon.reggie.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.roydon.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by Intellij IDEA
 * Author: yi cheng
 * Date: 2022/9/29
 * Time: 16:54
 **/
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {

    @Value("${files.upload.path}")
    private String uploadPath;

    /**
     * 文件上传
     *
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(@RequestPart("file") MultipartFile file) {
        // 前端upload组件用的ele，会自动生成一个input，它的name为file所以此方法参数名也需为file
        // file是一个临时文件，暂时存在c盘temp相关tomcat文件夹下，需要后续转存
        log.info("文件上传...");
//        try {
//            // 转存到指定目录，不推荐写死，一般在yml配置文件里配置
//            file.transferTo(new File("D:\\hello.jpg"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        /*转存文件配置好路径，可优化为*/
//        try {
//            // 转存到配置目录，文件名写死了，所以此方法也不推荐
//            file.transferTo(new File(uploadPath+"hello.jpg"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        //String originalFilename = file.getOriginalFilename();//获取原始文件名（上传的文件名）
        /*使用原始文件名的方式转存*/
//        try {
//            // 转存到配置目录，但文件名会重名发生覆盖，所以不推荐
//            String originalFilename = file.getOriginalFilename();
//            file.transferTo(new File(uploadPath + originalFilename));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        /*使用uuid生成随机文件名，避免重名覆盖问题*/
        String originalFilename = file.getOriginalFilename();//获取上传文件名，可以用字符串截取lastIndexOf，推荐hutool
        String fileType = FileUtil.extName(originalFilename);// jpg\png\jpeg...(不带点.)
        long fileSize = file.getSize();// 获取文件大小
        // 生成随机uuid作为文件名
        String uuid = IdUtil.fastSimpleUUID();// uuid也可以使用 UUID.randomUUID().toString();
        // 加上文件类型的完整文件名
        String fileUUIDName = uuid + StrUtil.DOT + fileType;// StrUtil.DOT 就是一个点（.）
        log.info("文件存储路径：{}，存储文件名：{}，文件大小约：{}", uploadPath, fileUUIDName, fileSize / 1024 + "K");
        // 如果配置文件指定的目录不存在则创建
        File dir = new File(uploadPath);
        if (!dir.exists()) {
            // 不存在就创建
            dir.mkdirs();
        }
        try {
            file.transferTo(new File(uploadPath + fileUUIDName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return R.success(fileUUIDName);
    }

    /**
     * 文件下载
     *
     * @param fileUUIDName
     * @param response     相应浏览器
     */
    @GetMapping("/download")
    public void download(@RequestParam("name") String fileUUIDName, HttpServletResponse response) {


        try {
            // 输入流读取文件
            FileInputStream fileInputStream = new FileInputStream(new File(uploadPath + fileUUIDName));
            // 输出流写入文件到浏览器
            ServletOutputStream outputStream = response.getOutputStream();

            // 代表响应文件为图片
            response.setContentType("image/jpeg");


            //开始读取
//            response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileUUIDName, "UTF-8"));
//            response.setContentType("application/octet-stream");
//            response.getOutputStream().write(FileUtil.readBytes(new File(fileUUIDName)));//一步搞定
            int len = 0;
            byte[] bytes = new byte[1024];

            while ((len = fileInputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
                outputStream.flush();// 刷新
            }
            // 关闭流
            outputStream.close();
            fileInputStream.close();


        } catch (Exception e) {
            e.printStackTrace();
        }


    }


}
