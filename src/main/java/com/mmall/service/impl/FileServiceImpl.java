package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.common.ServerResponse;
import com.mmall.service.FileService;
import com.mmall.util.FTPUtil;
import com.mmall.util.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

@Service("fileService")
public class FileServiceImpl implements FileService {
    private static Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);
    @Override
    public String upload(MultipartFile file, String localPath) {
        //上传分为三步，第一步拿到页面传来的file，存到本地
        //第二步把本地的file传到远程服务器ftp上
        //第三步把本地存储的file删除

        //首先存在本地
        String originName = file.getOriginalFilename();
        String newName = UUID.randomUUID().toString()+"."+originName.substring(originName.lastIndexOf(".")+1);
        File uploadDir = new File(localPath);
        if(!uploadDir.exists())
            uploadDir.mkdirs();
        File targetFile = new File(localPath,newName);
        try {
            file.transferTo(targetFile);
        } catch (IOException e) {
            logger.error("存储文件失败",e);
        }

        //上传到ftp服务器上
        if(!FTPUtil.uploadFiles(Lists.newArrayList(targetFile),PropertiesUtil.get("ftpfile.upload.remote")))
            return null;

        //删除本地文件
        targetFile.delete();

        return targetFile.getName();
    }
}
