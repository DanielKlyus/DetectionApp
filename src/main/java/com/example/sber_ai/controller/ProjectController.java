package com.example.sber_ai.controller;

import com.example.sber_ai.model.request.CreateProjectRequest;
import com.example.sber_ai.model.request.GetProjectRequest;
import com.example.sber_ai.model.response.CreateProjectResponse;
import com.example.sber_ai.model.response.GetProjectResponse;
import com.example.sber_ai.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequiredArgsConstructor
@RequestMapping("/projects")
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping("/create")
    public CreateProjectResponse createProject(@RequestBody CreateProjectRequest createProjectRequest) {
        return projectService.createProject(createProjectRequest);
    }

    @GetMapping("/get")
    public GetProjectResponse getProject(@RequestBody GetProjectRequest getProjectRequest) {
        return projectService.getProject(getProjectRequest);
    }

    @GetMapping("/get/from/{userId}")
    public ArrayList<GetProjectResponse> getUserProjects(@PathVariable Long userId) {
        return projectService.getUserProjects(userId);
    }
}
