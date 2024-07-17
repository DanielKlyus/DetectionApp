package com.example.sber_ai.service;

import com.example.sber_ai.model.request.ProjectRequest;
import com.example.sber_ai.model.response.ProjectResponse;

public interface ProjectService {
    public ProjectResponse createProject(ProjectRequest projectRequest);
}
