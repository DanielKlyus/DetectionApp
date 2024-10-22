package com.example.sber_ai.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateProjectRequest {
    @NotBlank(message = "Name cannot be empty")
    private String name;

    @NotBlank(message = "Path source cannot be empty")
    private String pathSource;

    @NotBlank(message = "Path save cannot be empty")
    private String pathSave;

    @NotNull(message = "User id cannot be null")
    private Long userId;
}
