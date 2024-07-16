package com.example.sber_ai.repository;

import com.example.sber_ai.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;


public interface ProjectRepository extends JpaRepository<Project, UUID> {
}
