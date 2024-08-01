package com.example.sber_ai.service.mapper;

import com.example.sber_ai.model.entity.Category;
import com.example.sber_ai.model.entity.Image;
import com.example.sber_ai.model.file.FileInfo;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

@Component
public class ImageMapper {

    public Image toEntity(FileInfo fileInfo, List<Category> categories) {
        Image image = new Image();
        image.setName(fileInfo.getName());
        File file = new File(fileInfo.getPath());
        image.setPath(file.getParent().substring(0, file.getParent().length() - "temp".length()) + file.getName());
        image.setDateTime(fileInfo.getDateTime());
        image.setMinioUrl(fileInfo.getMinioUrl());
        Category defaultCategory = categories.stream().filter(dbCategory -> "unknown".equals(dbCategory.getType())).findFirst().orElse(null);
        image.setCategoryId(categories.stream().filter(dbCategory -> dbCategory.getType().equals(fileInfo.getCategory())).findFirst().orElse(defaultCategory));
        image.setAnimalCount(fileInfo.getAnimalCount());
        image.setThreshold(fileInfo.getThreshold());
        return image;
    }
}
