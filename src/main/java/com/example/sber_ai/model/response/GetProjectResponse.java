package com.example.sber_ai.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetProjectResponse {
    private UUID id;

    private String name;

    private String pathSource;

    private String pathSave;

    private UUID userId;

    private boolean isActive;
}
