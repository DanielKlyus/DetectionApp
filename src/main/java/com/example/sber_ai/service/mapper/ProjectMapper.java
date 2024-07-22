package com.example.sber_ai.service.mapper;

import com.example.sber_ai.entity.Project;
import com.example.sber_ai.model.request.CreateProjectRequest;
import com.example.sber_ai.model.response.CreateProjectResponse;
import com.example.sber_ai.model.response.GetProjectResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProjectMapper {
    @Mapping(source = "user.id", target = "userId")
    CreateProjectResponse mapToResponse(Project project);

    @Mapping(source = "user.id", target = "userId")
    GetProjectResponse mapToGetResponse(Project project);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", constant = "true")
    Project mapToEntity(CreateProjectRequest request);
}
