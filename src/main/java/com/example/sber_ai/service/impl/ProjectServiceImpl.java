package com.example.sber_ai.service.impl;

import com.example.sber_ai.model.entity.Project;
import com.example.sber_ai.model.request.CreateProjectRequest;
import com.example.sber_ai.model.request.GetProjectRequest;
import com.example.sber_ai.model.response.CreateProjectResponse;
import com.example.sber_ai.model.response.GetProjectResponse;
import com.example.sber_ai.repository.ProjectRepository;
import com.example.sber_ai.repository.UserRepository;
import com.example.sber_ai.service.ProjectService;
import com.example.sber_ai.service.mapper.ProjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Setter
@Getter
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMapper projectMapper;

    @Override
    public CreateProjectResponse createProject(CreateProjectRequest createProjectRequest) {
        ArrayList<Project> projects = projectRepository.findAllByUserId(createProjectRequest.getUserId());
        for (Project p : projects) {
            p.setActive(false);
        }
        Project project = projectMapper.mapToEntity(createProjectRequest);
        projectRepository.save(project);

        return projectMapper.mapToResponse(project);
    }

    @Override
    public GetProjectResponse getProject(GetProjectRequest getProjectRequest) {
        Project project = projectRepository.getById(getProjectRequest.getId());
        return projectMapper.mapToGetResponse(project);
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
}
