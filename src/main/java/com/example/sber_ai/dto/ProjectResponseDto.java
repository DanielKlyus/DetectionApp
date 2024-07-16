package com.example.sber_ai.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
public class ProjectResponseDto {
    private UUID id;

    private String name;

    private String pathSource;

    private String pathSave;
}
