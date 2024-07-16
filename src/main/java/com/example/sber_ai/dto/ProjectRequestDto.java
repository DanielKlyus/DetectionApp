package com.example.sber_ai.dto;

import lombok.Data;
import lombok.NonNull;

import java.util.UUID;

@Data
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
