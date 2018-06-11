package com.mmall.service;

import org.springframework.web.multipart.MultipartFile;

public interface IFileServer {
    String upload(MultipartFile file, String path);
}
