package com.example.sber_ai.model.file;

import com.example.sber_ai.model.entity.Category;
import com.example.sber_ai.model.entity.Image;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class FileInfo {

    private String name;

    private String path;

    private String minioUrl;

    private String category;

    private Integer animalCount;

    private Double threshold;

    private int originalWidth;

    private int originalHeight;

    @JsonIgnore
    private MultipartFile file;

    public FileInfo(String name, String path, MultipartFile file, int originalWidth, int originalHeight) {
        this.name = name;
        this.path = path;
        this.file = file;
        this.originalWidth = originalWidth;
        this.originalHeight = originalHeight;
    }

    public Image toEntity(FileInfo fileInfo, List<Category> categories) {
        Image image = new Image();
        image.setName(fileInfo.getName());
        image.setPath(fileInfo.getPath());
        image.setMinioUrl(fileInfo.getMinioUrl());
        image.setCategoryId(categories.stream().filter(dbCategory -> dbCategory.getType().equals(fileInfo.getCategory())).findFirst().orElse(null));
        image.setAnimalCount(fileInfo.getAnimalCount());
        image.setThreshold(fileInfo.getThreshold());
        return image;
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", minioUrl='" + minioUrl + '\'' +
                ", category='" + category + '\'' +
                ", animalCount=" + animalCount +
                ", threshold=" + threshold +
                ", originalWidth=" + originalWidth +
                ", originalHeight=" + originalHeight +
                '}';
    }

}
