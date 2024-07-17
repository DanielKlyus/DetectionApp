package com.example.sber_ai.service.mapper;

import com.example.sber_ai.entity.Project;
import com.example.sber_ai.model.response.ProjectResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ProjectMapper {
    @Mapping(source = "user.id", target = "userId")
    ProjectResponse mapToResponse(Project project);
}
