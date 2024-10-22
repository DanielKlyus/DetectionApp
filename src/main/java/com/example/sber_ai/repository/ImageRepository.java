package com.example.sber_ai.repository;

import com.example.sber_ai.model.entity.Category;
import com.example.sber_ai.model.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findAllByCategoryId(Category categoryId);

    @Query("SELECT i FROM Image i INNER JOIN i.categoryId c WHERE c.classType = 'animal' AND c.projectId.id = :project AND i.dateTime IS NOT NULL")
    List<Image> findAllAnimalImagesAndDatetimeIsNotNullByProject(@Param("project") Long projectId);
}
