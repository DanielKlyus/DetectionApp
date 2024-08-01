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

    @GetMapping("/{projectId}/start")
    public ResponseEntity<Void> startProject(@PathVariable Long projectId) {
        projectService.startProject(projectId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{projectId}/get")
    public GetProjectResponse getProject(@PathVariable Long projectId) {
        return projectService.getProject(projectId);
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

    @PostMapping("/{projectId}/countPassages")
    public ResponseEntity<Void> countPassages(@PathVariable Long projectId, @RequestParam Long minutes) {
        projectService.countPassages(projectId, minutes);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{projectId}/categories/{categoryId}/delete")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long projectId, @PathVariable Long categoryId) {
        projectService.deleteCategory(categoryId);
        return ResponseEntity.ok().build();
    }
}
