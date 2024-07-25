package com.example.sber_ai.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "species")
    private String species;

    @Column(name = "img", columnDefinition = "TEXT", length = 350)
    private String img;

    @Column(name = "class")
    private String classType;

    @Column(name = "type")
    private String type;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project projectId;

    public Category(String name, String species, String classType, String type) {
        this.name = name;
        this.species = species;
        this.classType = classType;
        this.type = type;
    }
}
