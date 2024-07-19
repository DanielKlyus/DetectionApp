package com.example.sber_ai.model.request;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateProjectRequest {
    private String name;

    private String pathSource;

    private String pathSave;

    private UUID userId;
}
