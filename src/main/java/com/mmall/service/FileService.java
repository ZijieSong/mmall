package com.mmall.service;

import com.mmall.common.ServerResponse;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    String upload(MultipartFile file, String localPath);
}
