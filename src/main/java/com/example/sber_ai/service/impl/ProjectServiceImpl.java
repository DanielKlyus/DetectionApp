package com.example.sber_ai.service.impl;

import com.example.sber_ai.entity.Project;
import com.example.sber_ai.model.request.ProjectRequest;
import com.example.sber_ai.model.response.ProjectResponse;
import com.example.sber_ai.repository.ProjectRepository;
import com.example.sber_ai.repository.UserRepository;
import com.example.sber_ai.service.ProjectService;
import com.example.sber_ai.service.mapper.ProjectMapper;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Setter
@Getter
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private ProjectMapper projectMapper;
    @Override
    public ProjectResponse createProject(ProjectRequest projectRequest) {
        Project project = new Project();
        project.setName(projectRequest.getName());
        project.setPathSource(projectRequest.getPathSource());
        project.setPathSave(projectRequest.getPathSave());
        project.setUser(userRepository.getById(projectRequest.getUserId()));
        project.setActive(true);
        projectRepository.save(project);

        return projectMapper.mapToResponse(project);
    }
}
