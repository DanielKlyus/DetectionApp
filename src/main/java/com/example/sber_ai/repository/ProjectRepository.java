package com.example.sber_ai.repository;

import com.example.sber_ai.model.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    ArrayList<Project> findAllByUserId(Long id);

    Optional<Project> findByName(String name);
}
