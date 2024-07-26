package com.example.sber_ai.service.impl;

import com.example.sber_ai.exception.CategoryException;
import com.example.sber_ai.exception.CreateBucketException;
import com.example.sber_ai.exception.ImageException;
import com.example.sber_ai.model.file.FileInfo;
import com.example.sber_ai.service.MinioService;
import com.ibm.icu.text.Transliterator;
import io.minio.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class MinioServiceImpl implements MinioService {
    private static final Transliterator toLatinTrans = Transliterator.getInstance("Russian-Latin/BGN");

    private final MinioClient minioClient;

    public static String transliterateAndFormat(String text) {
        String transliterated = toLatinTrans.transliterate(text);
        return transliterated.toLowerCase().replaceAll("[^a-z0-9]", "");
    }

    @Override
    public void uploadSourceFiles(List<FileInfo> files, String projectName) {
        if (!projectName.matches("[a-z0-9]+")){
            projectName = transliterateAndFormat(projectName);
        }

        if (!createBucket(projectName)) {
            log.error("Cannot create bucket: {}", projectName);
        }

        String finalProjectName = projectName;
        files.forEach(fileInfo -> {
            try {
                minioClient.putObject(PutObjectArgs.builder()
                        .bucket(finalProjectName)
                        .object(fileInfo.getName())
                        .stream(fileInfo.getFile().getInputStream(), fileInfo.getFile().getSize(), -1)
                        .build());

                fileInfo.setMinioUrl(minioClient.getPresignedObjectUrl(
                        GetPresignedObjectUrlArgs.builder()
                                .method(Method.GET)
                                .bucket(finalProjectName)
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
    public String uploadFile(MultipartFile categoryImg) {
        try {
            String categoryName = categoryImg.getOriginalFilename();
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket("categories")
                    .object(categoryName)
                    .stream(categoryImg.getInputStream(), categoryImg.getSize(), -1)
                    .build());

            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket("categories")
                            .object(categoryName)
                            .expiry(10, TimeUnit.MINUTES)
                            .build());

        } catch (Exception e) {
            log.error("Unexpected exception when create a bucket: {}", e.getMessage());
            throw new CreateBucketException(e.getLocalizedMessage());
        }
    }

    @Override
    public String getMinioCategoryUrl(String categoryType) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket("categories")
                            .object(categoryType)
                            .expiry(10, TimeUnit.MINUTES)
                            .build());
        } catch (Exception e) {
            log.error("Category {} not found", categoryType);
            throw new CategoryException("Category not found ");
        }
    }

    @Override
    public boolean createBucket(String name) {
        try {
            BucketExistsArgs beArgs = BucketExistsArgs.builder()
                    .bucket(name)
                    .build();

            if (!minioClient.bucketExists(beArgs)) {
                MakeBucketArgs args = MakeBucketArgs.builder()
                        .bucket(name)
                        .build();
                minioClient.makeBucket(args);
                log.info("Bucket {} created", name);
            } else {
                log.info("Bucket {} already exists", name);
            }
            return true;
        } catch (Exception e) {
            log.error("Unexpected exception when create a bucket: {}", e.getMessage());
            throw new CreateBucketException(e.getLocalizedMessage());
        }
    }

}