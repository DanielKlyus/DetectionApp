package com.example.sber_ai.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "images")
public class Image {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "path")
    private String path;

    @Column(name = "minio_url", length = 350)
    private String minioUrl;

    @Column(name = "datetime", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private LocalDateTime dateTime;

    @Column(name = "animal_count")
    private Integer animalCount;

    @Column(name = "passage")
    private Integer passage;

    @Column(name = "animal_count_in_passage")
    private Integer animalCountInPassage;

    @Column(name = "threshold")
    private Double threshold;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category categoryId;
}
