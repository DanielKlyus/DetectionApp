package com.example.sber_ai.repository;

import com.example.sber_ai.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {
    ArrayList<Project> findAllByUserId(UUID id);
}
