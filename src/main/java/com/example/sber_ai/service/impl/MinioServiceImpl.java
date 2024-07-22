package com.example.sber_ai.service.impl;

import com.example.sber_ai.exception.CreateBucketException;
import com.example.sber_ai.exception.ImageException;
import com.example.sber_ai.model.file.FileInfo;
import com.example.sber_ai.service.MinioService;
import io.minio.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class MinioServiceImpl implements MinioService {

    private final MinioClient minioClient;

    @Override
    public void upload(List<FileInfo> files, String projectName) {
        if (!createBucketByProjectName(projectName)) {
            log.error("Cannot create bucket: {}", projectName);
        }

        files.forEach(fileInfo -> {
            try {
                minioClient.putObject(PutObjectArgs.builder()
                        .bucket(projectName)
                        .object(fileInfo.getName())
                        .stream(fileInfo.getFile().getInputStream(), fileInfo.getFile().getSize(), -1)
                        .build());

                fileInfo.setMinioUrl(minioClient.getPresignedObjectUrl(
                        GetPresignedObjectUrlArgs.builder()
                                .method(Method.GET)
                                .bucket(projectName)
                                .object(fileInfo.getName())
                                .expiry(10, TimeUnit.MINUTES)
                                .build()));
            } catch (Exception e) {
                log.error("Cannot upload file: {}", e.getMessage());
                throw new ImageException("Cannot upload file");
            }
        });
    }

    @Override
    public boolean createBucketByProjectName(String projectName) {
        try {
            BucketExistsArgs beArgs = BucketExistsArgs.builder()
                    .bucket(projectName)
                    .build();

            if (!minioClient.bucketExists(beArgs)) {
                MakeBucketArgs args = MakeBucketArgs.builder()
                        .bucket(projectName)
                        .build();
                minioClient.makeBucket(args);
                log.info("Bucket {} created", projectName);
            } else {
                log.info("Bucket {} already exists", projectName);
            }
            return true;
        } catch (Exception e) {
            log.error("Unexpected exception when create a bucket: {}", e.getMessage());
            throw new CreateBucketException(e.getLocalizedMessage());
        }
    }
}