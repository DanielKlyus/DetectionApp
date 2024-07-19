package com.example.sber_ai.service;

import com.example.sber_ai.model.request.CreateProjectRequest;
import com.example.sber_ai.model.request.GetProjectRequest;
import com.example.sber_ai.model.response.CreateProjectResponse;
import com.example.sber_ai.model.response.GetProjectResponse;

import java.util.ArrayList;
import java.util.UUID;

public interface ProjectService {
    public CreateProjectResponse createProject(CreateProjectRequest createProjectRequest);

    public GetProjectResponse getProject(GetProjectRequest getProjectRequest);

    public ArrayList<GetProjectResponse> getUserProjects(UUID projectId);
}
