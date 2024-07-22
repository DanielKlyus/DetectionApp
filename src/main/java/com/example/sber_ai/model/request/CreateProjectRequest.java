package com.example.sber_ai.model.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateProjectRequest {
    private String name;

    private String pathSource;

    private String pathSave;

    private Long userId;
}
