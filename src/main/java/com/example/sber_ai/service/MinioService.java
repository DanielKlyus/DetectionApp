package com.example.sber_ai.service;

import com.example.sber_ai.model.file.FileInfo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MinioService {
    void uploadSourceFiles(List<FileInfo> files, String projectName);

    boolean createBucket(String name);

    String uploadFile(MultipartFile categoryImg);

    String getMinioCategoryUrl(String categoryType);

}
