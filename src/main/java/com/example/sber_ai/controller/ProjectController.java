package com.example.sber_ai.controller;

import com.example.sber_ai.model.request.CreateCategoryRequest;
import com.example.sber_ai.model.request.CreateProjectRequest;
import com.example.sber_ai.model.response.*;
import com.example.sber_ai.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/projects")
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping("/create")
    public CreateProjectResponse createProject(@RequestBody CreateProjectRequest createProjectRequest) {
        return projectService.createProject(createProjectRequest);
    }

    @GetMapping("/{id}/start")
    public ResponseEntity<Void> initProject(@PathVariable Long id) {
        projectService.initProject(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/get")
    public GetProjectResponse getProject(@PathVariable Long id) {
        return projectService.getProject(id);
    }

    @GetMapping("/get/from/{userId}")
    public List<GetProjectResponse> getUserProjects(@PathVariable Long userId) {
        return projectService.getUserProjects(userId);
    }

    @PostMapping("/{projectId}/categories/create")
    public CreateCategoryResponse createCategory(@PathVariable Long projectId, @ModelAttribute CreateCategoryRequest createCategoryRequest) {
        return projectService.createCategory(createCategoryRequest, projectId);
    }

    @GetMapping("/{projectId}/categories/{categoryId}")
    public List<GetImageResponse> getCategoryImages(@PathVariable Long projectId, @PathVariable Long categoryId) {
        return projectService.getCategoryImages(projectId, categoryId);
    }

    @GetMapping("/{projectId}/categories/get")
    public List<GetCategoriesResponse> getCategories(@PathVariable Long projectId) {
        return projectService.getCategories(projectId);
    }

    @PostMapping("/{projectId}/categories/{categoryId}/image/{imageId}/change")
    public ResponseEntity<Void> changeCategory(@PathVariable Long projectId, @PathVariable Long categoryId, @PathVariable Long imageId, @RequestParam Long newCategoryId) {
        projectService.changeCategory(imageId, newCategoryId);
        return ResponseEntity.ok().build();
    }
}
