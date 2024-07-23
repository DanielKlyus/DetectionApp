package com.example.sber_ai.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "images")
public class Image {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "name", columnDefinition = "TEXT")
    private String name;

    @Column(name = "path", columnDefinition = "TEXT")
    private String path;

    @Column(name = "minio_url", columnDefinition = "TEXT", length = 350)
    private String minioUrl;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project projectId;
}
