package com.example.sber_ai.repository;

import com.example.sber_ai.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
