package com.example.sber_ai.model.file;

import com.example.sber_ai.model.entity.Category;
import com.example.sber_ai.model.entity.Image;
import com.example.sber_ai.service.mapper.ImageMapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class FileInfo {

    private String name;

    private String path;

    private String minioUrl;

    private String category;

    private Date dateTime;

    private Integer animalCount;

    private Double threshold;

    private int originalWidth;

    private int originalHeight;

    @JsonIgnore
    private MultipartFile file;

    public FileInfo(String name, String path, MultipartFile file, Date dateTime, int originalWidth, int originalHeight) {
        this.name = name;
        this.path = path;
        this.file = file;
        this.dateTime = dateTime;
        this.originalWidth = originalWidth;
        this.originalHeight = originalHeight;
    }

    public Image toEntity(FileInfo fileInfo, List<Category> categories) {
        ImageMapper imageMapper = new ImageMapper();
        return imageMapper.toEntity(fileInfo, categories);
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", dateTime=" + dateTime +
                ", minioUrl='" + minioUrl + '\'' +
                ", animalCount=" + animalCount +
                ", threshold=" + threshold +
                ", originalWidth=" + originalWidth +
                ", originalHeight=" + originalHeight +
                '}';
    }

}
