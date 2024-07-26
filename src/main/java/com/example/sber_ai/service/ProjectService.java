package com.example.sber_ai.service;

import com.example.sber_ai.model.request.CreateCategoryRequest;
import com.example.sber_ai.model.request.CreateProjectRequest;
import com.example.sber_ai.model.response.*;

import java.util.List;

public interface ProjectService {
    CreateProjectResponse createProject(CreateProjectRequest createProjectRequest);

    GetProjectResponse getProject(Long projectId);

    List<GetProjectResponse> getUserProjects(Long projectId);

    CreateCategoryResponse createCategory(CreateCategoryRequest createCategoryRequest, Long projectId);

    List<GetImageResponse> getCategoryImages(Long projectId, Long categoryId);

    void initProject(Long id);

    List<GetCategoriesResponse> getCategories(Long projectId);

    void changeCategory(Long imageId, Long newCategoryId);

    void deleteCategory(Long categoryId);
}
