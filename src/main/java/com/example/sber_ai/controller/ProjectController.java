package com.example.sber_ai.controller;

import com.example.sber_ai.exception.AuthException;
import com.example.sber_ai.model.request.CreateCategoryRequest;
import com.example.sber_ai.model.request.CreateProjectRequest;
import com.example.sber_ai.model.response.*;
import com.example.sber_ai.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Project controller", description = "Эндпоинты для работы с проектами")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/projects")
public class ProjectController {
    private final ProjectService projectService;

    @Operation(
            summary = "Создание проекта",
            description = "Проверяет валидность входящих параметров и создает проект")
    @PostMapping("/create")
    public CreateProjectResponse createProject(@Valid @RequestBody CreateProjectRequest createProjectRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        if (!String.valueOf(createProjectRequest.getUserId()).equals(userDetails.getUsername())) {
            throw new AuthException("Access denied");
        }

        return projectService.createProject(createProjectRequest);
    }

    @Operation(summary = "Запуск проекта и обработка переданных фотографий")
    @PostMapping("/{projectId}/start")
    public ResponseEntity<Void> startProject(@PathVariable Long userId, @PathVariable Long projectId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        if (!String.valueOf(userId).equals(userDetails.getUsername())) {
            throw new AuthException("Access denied");
        }
        projectService.startProject(projectId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Выбор определённого проекта")
    @GetMapping("/{projectId}/get")
    public GetProjectResponse getProject(@PathVariable Long userId, @PathVariable Long projectId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        if (!String.valueOf(userId).equals(userDetails.getUsername())) {
            throw new AuthException("Access denied");
        }
        return projectService.getProject(projectId);
    }

    @Operation(summary = "Выбор всех проектов пользователя")
    @GetMapping("/get")
    public List<GetProjectResponse> getUserProjects(@PathVariable Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        if (!String.valueOf(userId).equals(userDetails.getUsername())) {
            throw new AuthException("Access denied");
        }
        return projectService.getUserProjects(userId);
    }

    @Operation(
            summary = "Создание категории в проекте",
            description = "Проверяет валидность входящих параметров и создает категорию")
    @PostMapping("/{projectId}/categories/create")
    public CreateCategoryResponse createCategory(@PathVariable Long userId, @PathVariable Long projectId, @Valid @ModelAttribute CreateCategoryRequest createCategoryRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        if (!String.valueOf(userId).equals(userDetails.getUsername())) {
            throw new AuthException("Access denied");
        }
        return projectService.createCategory(createCategoryRequest, projectId);
    }

    @Operation(summary = "Выбор определённой категории в проекте")
    @GetMapping("/{projectId}/categories/{categoryId}")
    public List<GetImageResponse> getCategoryImages(@PathVariable Long userId, @PathVariable Long projectId, @PathVariable Long categoryId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        if (!String.valueOf(userId).equals(userDetails.getUsername())) {
            throw new AuthException("Access denied");
        }
        return projectService.getCategoryImages(projectId, categoryId);
    }

    @Operation(summary = "Выбор всех категорий в проекте")
    @GetMapping("/{projectId}/categories/get")
    public List<GetCategoriesResponse> getCategories(@PathVariable Long userId, @PathVariable Long projectId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        if (!String.valueOf(userId).equals(userDetails.getUsername())) {
            throw new AuthException("Access denied");
        }
        return projectService.getCategories(projectId);
    }

    @Operation(summary = "Изменение категории фотографии в проекте")
    @PostMapping("/{projectId}/categories/{categoryId}/images/{imageId}/change")
    public ResponseEntity<Void> changeCategory(@PathVariable Long userId, @PathVariable Long projectId, @PathVariable Long categoryId, @PathVariable Long imageId, @RequestParam Long newCategoryId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        if (!String.valueOf(userId).equals(userDetails.getUsername())) {
            throw new AuthException("Access denied");
        }
        projectService.changeCategory(imageId, newCategoryId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Просчет проходов")
    @PostMapping("/{projectId}/countPassages")
    public ResponseEntity<Void> countPassages(@PathVariable Long projectId, @RequestParam Long minutes) {
        projectService.countPassages(projectId, minutes);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Удаление категории в проекте")
    @DeleteMapping("/{projectId}/categories/{categoryId}/delete")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long userId, @PathVariable Long projectId, @PathVariable Long categoryId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        if (!String.valueOf(userId).equals(userDetails.getUsername())) {
            throw new AuthException("Access denied");
        }
        projectService.deleteCategory(categoryId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Сохранение фотографий",
            description = "Выполняет сохранение фотографий, отсортированных по категориям")
    @GetMapping("/{projectId}/save")
    public ResponseEntity<Void> saveImages(@PathVariable Long userId, @PathVariable Long projectId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        if (!String.valueOf(userId).equals(userDetails.getUsername())) {
            throw new AuthException("Access denied");
        }
//        projectService.saveImages(type, projectId);
        return ResponseEntity.ok().build();
    }
}
