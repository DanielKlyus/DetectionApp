package com.example.sber_ai.service;

import com.example.sber_ai.model.request.CreateProjectRequest;
import com.example.sber_ai.model.request.GetProjectRequest;
import com.example.sber_ai.model.response.CreateProjectResponse;
import com.example.sber_ai.model.response.GetProjectResponse;

import java.util.List;

public interface ProjectService {
    CreateProjectResponse createProject(CreateProjectRequest createProjectRequest);

    GetProjectResponse getProject(GetProjectRequest getProjectRequest);

    List<GetProjectResponse> getUserProjects(Long projectId);
}
