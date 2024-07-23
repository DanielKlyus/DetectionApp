package com.example.sber_ai.service;

import com.example.sber_ai.model.file.FileInfo;

import java.util.List;

public interface ImageService {
    void uploadSourceFiles(String directoryPath, String projectName, String pathSave);

    void saveAll(List<FileInfo> files, String projectName);
}
