package com.example.sber_ai.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
public class ProjectResponseDto {
    private UUID id;

    private String name;

    private String pathSource;

    private String pathSave;
}
