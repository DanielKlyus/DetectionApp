package com.example.sber_ai.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ProjectResponseDto {
    private UUID id;

    private String name;

    private String pathSource;

    private String pathSave;
}
