package com.example.sber_ai.service.impl;

import com.example.sber_ai.model.response.ServerResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;

import java.util.Optional;

@Slf4j
class ImageServiceImplTest {

    @Value("${ml.server.url}")
    private String url;

    @Value("${x.workspace.id}")
    private String workspaceId;

    @Value("${server.auth.username}")
    private String serverAuthUsername;

    @Value("${server.auth.password}")
    private String serverAuthPassword;

    private ImageServiceImpl imageService;

    @BeforeEach
    void setUp() {
        ObjectMapper mockMapper = new ObjectMapper();
        imageService = new ImageServiceImpl(mockMapper);
        imageService.setUrl(url);
        imageService.setWorkspaceId(workspaceId);
        imageService.setServerAuthUsername(serverAuthUsername);
        imageService.setServerAuthPassword(serverAuthPassword);
    }

    @Test
    void sendImage() {
        String fileName = "src/test/resources/pictures/gimalayBear.png";

        Optional<ServerResponse> result = imageService.sendImage(fileName);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isPresent());
    }
}