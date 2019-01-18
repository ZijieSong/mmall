package com.mmall.util;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class FTPUtil {
    private static final String ip = PropertiesUtil.get("ftp.server.ip");
    private static final String port = PropertiesUtil.get("ftp.server.port", "21");
    private static final String user = PropertiesUtil.get("ftp.user");
    private static final String pass = PropertiesUtil.get("ftp.pass");

    private FTPClient ftpClient;

    private static Logger logger = LoggerFactory.getLogger(FTPUtil.class);

    /**
     * 上传到ftp
     * @param files  上传的文件
     * @param remote 远程路径
     * @return
     */
    public static boolean uploadFiles(List<File> files, String remote) {
        FTPUtil ftpUtil = new FTPUtil();
        ftpUtil.ftpClient = new FTPClient();
        return ftpUtil.upload(files,remote);
    }

    private boolean upload(List<File> files, String remote) {
        if (!connect(ip, Integer.valueOf(port), user, pass))
            return false;
        try {
            if (!ftpClient.changeWorkingDirectory(remote))
                ftpClient.makeDirectory(remote);
            ftpClient.changeWorkingDirectory(remote);
            ftpClient.setBufferSize(1024);
            ftpClient.setControlEncoding("UTF-8");
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftpClient.enterLocalPassiveMode();

            for (File file : files) {
                FileInputStream inputStream = new FileInputStream(file);
                //上传到ftpServer
                ftpClient.storeFile(file.getName(),inputStream);
                inputStream.close();
            }
            return true;
        } catch (IOException e) {
            logger.error("上传失败", e);
            return false;
        } finally {
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                logger.error("关闭失败",e);
            }
        }
    }


    private boolean connect(String ip, Integer port, String user, String pass) {
        try {
            ftpClient.connect(ip, port);
            return ftpClient.login(user, pass);
        } catch (IOException e) {
            logger.error("连接失败", e);
            return false;
        }

    }

}
