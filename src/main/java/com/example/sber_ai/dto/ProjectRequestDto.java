package com.example.sber_ai.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter

public class ProjectRequestDto {
    @NonNull
    private String name;

    @NonNull
    private String pathSource;

    @NonNull
    private String pathSave;

    @NonNull
    private UUID userId;
}
