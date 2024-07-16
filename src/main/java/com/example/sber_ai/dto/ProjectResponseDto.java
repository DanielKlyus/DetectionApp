package com.example.sber_ai.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ProjectResponseDto {
    private UUID id;

    private String name;

    private String pathSource;

    private String pathSave;
}
