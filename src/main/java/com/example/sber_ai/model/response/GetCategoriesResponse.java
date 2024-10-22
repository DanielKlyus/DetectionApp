package com.example.sber_ai.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetCategoriesResponse {
    private Long id;

    private String name;

    private String species;

    private String img;

    private String classType;

    private String type;
}
