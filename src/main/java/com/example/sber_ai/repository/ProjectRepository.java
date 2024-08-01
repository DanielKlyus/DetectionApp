package com.example.sber_ai.repository;

import com.example.sber_ai.model.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    ArrayList<Project> findAllByUserId(Long id);

    Project findByName(String name);
}
