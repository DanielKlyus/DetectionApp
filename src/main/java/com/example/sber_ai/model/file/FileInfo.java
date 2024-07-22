package com.example.sber_ai.model.file;

import com.example.sber_ai.model.entity.Image;
import com.example.sber_ai.model.entity.Project;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class FileInfo {

    private String name;

    private String path;

    private String minioUrl;

    @JsonIgnore
    private MultipartFile file;

    public FileInfo(String name, String path, MultipartFile file) {
        this.name = name;
        this.path = path;
        this.file = file;
    }

    @Override
    public String toString() {
        return "FileInfo {" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", minioUrl='" + minioUrl + '\'' +
                "}\n";
    }

    public Image toEntity(FileInfo fileInfo, Project projectId) {
        Image image = new Image();
        image.setName(fileInfo.getName());
        image.setPath(fileInfo.getPath());
        image.setMinioUrl(fileInfo.getMinioUrl());
        image.setProjectId(projectId);
        return image;
    }
}
