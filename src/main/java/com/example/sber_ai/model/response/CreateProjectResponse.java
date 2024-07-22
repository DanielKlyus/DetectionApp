package com.example.sber_ai.model.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateProjectResponse {
    private Long id;

    private String name;

    private String pathSource;

    private String pathSave;

    private Long userId;

    private boolean isActive;
}
