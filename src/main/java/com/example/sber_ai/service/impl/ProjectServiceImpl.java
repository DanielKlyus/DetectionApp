package com.example.sber_ai.service.impl;

import com.example.sber_ai.exception.CategoryException;
import com.example.sber_ai.exception.ImageException;
import com.example.sber_ai.exception.ProjectException;
import com.example.sber_ai.model.entity.Category;
import com.example.sber_ai.model.entity.Image;
import com.example.sber_ai.model.entity.Project;
import com.example.sber_ai.model.request.CreateCategoryRequest;
import com.example.sber_ai.model.request.CreateProjectRequest;
import com.example.sber_ai.model.response.*;
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

import java.util.*;
import java.util.concurrent.TimeUnit;

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
        Project project = projectMapper.mapToEntity(createProjectRequest);
        projectRepository.save(project);

        for (Category c : defaultCategories.getCategories()) {
            c.setProjectId(project);
            c.setImg(imageService.getMinioCategoryUrl(c.getType()));
        }
        categoryRepository.saveAll(defaultCategories.getCategories());

        log.info("Project with name {} created", project.getName());
        return projectMapper.mapToResponse(project);
    }

    @Override
    public void startProject(Long id) {
        Project project = projectRepository.findById(id).orElseThrow(() -> new ProjectException("Project with id " + id + " not found"));
        imageService.uploadSourceFiles(project.getPathSource(), project.getName(), project.getPathSave());
        log.info("Project with id {} started", id);
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

        Project project = projectRepository.findById(projectId).orElseThrow(() -> new ProjectException("Project with id " + projectId + " not found"));
        Category category = categoryMapper.mapToEntity(createCategoryRequest, categoryUrl, project);
        categoryRepository.save(category);

        log.info("Category with type {} uploaded", category.getName());
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
    public List<GetCategoriesResponse> getCategories(Long projectId) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new ProjectException("Project with id " + projectId + " not found"));
        List<Category> categories = categoryRepository.findAllByProjectId(project);
        return categories.stream().map(projectMapper::mapToGetCategoriesResponse).toList();
    }

    @Override
    public void changeCategory(Long imageId, Long newCategoryId) {
        Image image = imageRepository.findById(imageId).orElseThrow(() -> new ImageException("Image with id " + imageId + " not found"));
        Category category = categoryRepository.findById(newCategoryId).orElseThrow(() -> new CategoryException("Category with id " + newCategoryId + " not found"));
        image.setCategoryId(category);
        image.setThreshold(1.0);
        imageRepository.save(image);
        log.info("Image with id {} changed to category with id {}", imageId, newCategoryId);
    }

    @Override
    public void deleteCategory(Long categoryId) {
        categoryRepository.deleteById(categoryId);
        log.info("Category with id {} deleted", categoryId);
    }

    @Override
    public void countPassages(Long projectId, Long minutes) {
        List<Image> images = imageRepository.findAllAnimalImagesAndDatetimeIsNotNullByProject(projectId);
        images.sort(Comparator.comparing(Image::getDateTime));
        int currentPassage = 0;
        int maxAnimals = 0;
        Stack<Image> stack = new Stack<>();
        for (Image image : images) {
            if (stack.isEmpty()) {
                // Начало проходов
                currentPassage++;
                image.setPassage(currentPassage);
                maxAnimals = image.getAnimalCount();
                stack.push(image);
            } else if (image.getCategoryId() != stack.peek().getCategoryId() ||
                    image.getDateTime().getTime() - stack.peek().getDateTime().getTime() > TimeUnit.MINUTES.toMillis(minutes)) {
                // Завершение текущего прохода
                while (!stack.isEmpty()) {
                    stack.pop().setAnimalCountInPassage(maxAnimals);
                }
                currentPassage++;
                maxAnimals = image.getAnimalCount();
                image.setPassage(currentPassage);
                stack.push(image);
            } else {
                // Продолжение текущего прохода
                image.setPassage(currentPassage);
                maxAnimals = Math.max(maxAnimals, image.getAnimalCount());
                stack.push(image);
            }
        }
        // Завершение последнего прохода
        while (!stack.isEmpty()) {
            stack.pop().setAnimalCountInPassage(maxAnimals);
        }

        images.forEach(image -> {
            imageRepository.save(image);
            log.info("\nImage: {}, passage: {}, animalsInPassage: {}", image.getName(), image.getPassage(), image.getAnimalCountInPassage());
        });
    }
}
