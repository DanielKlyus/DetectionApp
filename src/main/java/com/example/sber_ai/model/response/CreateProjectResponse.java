package com.example.sber_ai.model.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateProjectResponse {
    private UUID id;

    private String name;

    private String pathSource;

    private String pathSave;

    private UUID userId;

    private boolean isActive;
}
