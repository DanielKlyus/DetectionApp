package com.example.sber_ai.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

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

    @Column(name = "minio_url", length = 450)
    private String minioUrl;

    @Column(name = "datetime", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Date dateTime;

    @Column(name = "animal_count")
    private Integer animalCount;

    @Column(name = "passage", nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer passage = 0;

    @Column(name = "animal_count_in_passage", nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer animalCountInPassage = 0;

    @Column(name = "threshold")
    private Double threshold;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category categoryId;

    @PrePersist
    public void prePersist() {
        if (this.animalCountInPassage == null) {
            this.animalCountInPassage = 0;
        }
        if (this.passage == null) {
            this.passage = 0;
        }
    }
}
