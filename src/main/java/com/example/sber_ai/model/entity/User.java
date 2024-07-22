package com.example.sber_ai.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @Column(name = "is_admin")
    private boolean isAdmin;
}
