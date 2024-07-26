package com.example.sber_ai.repository;

import com.example.sber_ai.model.entity.Category;
import com.example.sber_ai.model.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findAllByCategoryId(Category categoryId);
}
