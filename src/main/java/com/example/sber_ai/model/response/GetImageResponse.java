package com.example.sber_ai.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetImageResponse {
    private Long id;

    private String name;

    private String path;

    private String minioUrl;

    private String dateTime;

    private Integer animalCount;

    private Integer passage;

    private Integer animalCountInPassage;

    private Double threshold;
}
