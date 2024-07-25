package com.example.sber_ai.repository;

import com.example.sber_ai.model.entity.Category;
import com.example.sber_ai.model.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByProjectId(Project project);
}
