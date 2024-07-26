package com.example.sber_ai.service.impl;

import com.example.sber_ai.exception.CategoryException;
import com.example.sber_ai.exception.ProjectException;
import com.example.sber_ai.model.entity.Category;
import com.example.sber_ai.model.entity.Project;
import com.example.sber_ai.model.request.CreateCategoryRequest;
import com.example.sber_ai.model.request.CreateProjectRequest;
import com.example.sber_ai.model.response.CreateCategoryResponse;
import com.example.sber_ai.model.response.CreateProjectResponse;
import com.example.sber_ai.model.response.GetImageResponse;
import com.example.sber_ai.model.response.GetProjectResponse;
import com.example.sber_ai.repository.CategoryRepository;
import com.example.sber_ai.repository.ImageRepository;
import com.example.sber_ai.repository.ProjectRepository;
import com.example.sber_ai.service.ImageService;
import com.example.sber_ai.service.ProjectService;
import com.example.sber_ai.service.mapper.CategoryMapper;
import com.example.sber_ai.service.mapper.ProjectMapper;
import com.example.sber_ai.util.DefaultCategories;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Setter
@Getter
@Slf4j
public class ProjectServiceImpl implements ProjectService {
    private final ImageRepository imageRepository;

    private final ProjectRepository projectRepository;

    private final CategoryRepository categoryRepository;

    private final ProjectMapper projectMapper;

    private final CategoryMapper categoryMapper;

    private final ImageService imageService;

    private final ModelMapper modelMapper;

    private final DefaultCategories defaultCategories;

    @Override
    public CreateProjectResponse createProject(CreateProjectRequest createProjectRequest) {
        ArrayList<Project> projects = projectRepository.findAllByUserId(createProjectRequest.getUserId());
        for (Project p : projects) {
            p.setIsActive(false);
        }

        Project project = projectMapper.mapToEntity(createProjectRequest);
        projectRepository.save(project);

        for (Category c : defaultCategories.getCategories()) {
            c.setProjectId(project);
            c.setImg(imageService.getMinioCategoryUrl(c.getType()));
        }
        categoryRepository.saveAll(defaultCategories.getCategories());

        return projectMapper.mapToResponse(project);
    }

    @Override
    public GetProjectResponse getProject(Long id) {
        Optional<Project> project = projectRepository.findById(id);
        if (project.isPresent()) {
            return projectMapper.mapToGetResponse(project.get());
        } else {
            log.error("Project with id {} not found", id);
            throw new ProjectException("Project with id " + id + " not found");
        }
    }

    @Override
    public ArrayList<GetProjectResponse> getUserProjects(Long projectId) {
        ArrayList<Project> projects = projectRepository.findAllByUserId(projectId);
        ArrayList<GetProjectResponse> responses = new ArrayList<>();
        for (Project p : projects) {
            responses.add(projectMapper.mapToGetResponse(p));
        }
        return responses;
    }

    @Override
    public CreateCategoryResponse createCategory(CreateCategoryRequest createCategoryRequest, Long projectId) {
        String categoryUrl = imageService.uploadFile(createCategoryRequest.getImg());

        Project project = projectRepository.findByIsActiveAndId(true, projectId);
        Category category = categoryMapper.mapToEntity(createCategoryRequest, categoryUrl, project);
        categoryRepository.save(category);

        return categoryMapper.mapToResponse(category);
    }

    @Override
    public List<GetImageResponse> getCategoryImages(Long projectId, Long categoryId) {
        Optional<Category> category = categoryRepository.findById(categoryId);
        if (category.isPresent()) {
            return imageRepository.findAllByCategoryId(category.get()).stream()
                    .map(image -> modelMapper.map(image, GetImageResponse.class))
                    .toList();
        } else {
            log.error("Cannot find category with id {}", categoryId);
            throw new CategoryException("Cannot find category with id ", categoryId);
        }
    }

    @Override
    public void initProject(Long id) {
        Project project = projectRepository.findById(id).orElseThrow(() -> new ProjectException("Project with id " + id + " not found"));
        imageService.uploadSourceFiles(project.getPathSource(), project.getName(), project.getPathSave());
    }
}
