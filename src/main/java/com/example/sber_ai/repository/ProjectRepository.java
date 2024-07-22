package com.example.sber_ai.repository;

import com.example.sber_ai.model.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface ProjectRepository extends JpaRepository<Project, Long> {
    Optional<Project> findByName(String name);
}
