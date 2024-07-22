package com.example.sber_ai.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "projects")
public class Project {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @Column(name = "path_source")
    private String pathSource;

    @Column(name = "path_save")
    private String pathSave;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userId;

}
