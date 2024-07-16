package com.example.sber_ai.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "projects")
public class Project {
    @Id
    @GeneratedValue
    private UUID id;

    private String name;

    @Column(name = "path_source")
    private String pathSource;

    @Column(name = "path_save")
    private String pathSave;

    @ManyToOne
    @Column(name = "user_id")
    private User userId;
}
