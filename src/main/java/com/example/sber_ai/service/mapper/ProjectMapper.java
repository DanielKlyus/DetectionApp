package com.example.sber_ai.service.mapper;

import com.example.sber_ai.model.entity.Category;
import com.example.sber_ai.model.entity.Project;
import com.example.sber_ai.model.request.CreateProjectRequest;
import com.example.sber_ai.model.response.CreateProjectResponse;
import com.example.sber_ai.model.response.GetCategoriesResponse;
import com.example.sber_ai.model.response.GetProjectResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectMapper {
    @Mapping(source = "user.id", target = "userId")
    CreateProjectResponse mapToResponse(Project project);

    @Mapping(source = "user.id", target = "userId")
    GetProjectResponse mapToGetResponse(Project project);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", constant = "true")
    @Mapping(source = "userId", target = "user.id")
    Project mapToEntity(CreateProjectRequest request);

    GetCategoriesResponse mapToGetCategoriesResponse(Category category);
}
