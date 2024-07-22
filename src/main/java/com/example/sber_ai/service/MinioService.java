package com.example.sber_ai.service;

import com.example.sber_ai.model.file.FileInfo;

import java.util.List;

public interface MinioService {
    void upload(List<FileInfo> files, String projectName);

    boolean createBucketByProjectName(String projectName);

}
