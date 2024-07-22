package com.example.sber_ai.model.dto;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

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
