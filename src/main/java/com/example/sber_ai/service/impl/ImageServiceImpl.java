package com.example.sber_ai.service.impl;

import com.example.sber_ai.exception.ImageException;
import com.example.sber_ai.exception.ObjectMapperException;
import com.example.sber_ai.model.response.ServerResponse;
import com.example.sber_ai.service.ImageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
@Setter
@Getter
public class ImageServiceImpl implements ImageService {
    private final ObjectMapper mapper;
    private OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(100, TimeUnit.SECONDS)
            .writeTimeout(100, TimeUnit.SECONDS)
            .readTimeout(100, TimeUnit.SECONDS)
            .build();
    @Value("${ml.server.url}")
    private String url;

    @Value("${x.workspace.id}")
    private String workspaceId;

    @Value("${server.auth.username}")
    private String serverAuthUsername;

    @Value("${server.auth.password}")
    private String serverAuthPassword;

    @Override
    public Optional<ServerResponse> sendImage(String fileName) {
        int maxAttempts = 5;

        for (int attempt = 0; attempt < maxAttempts; ++attempt) {

            byte[] imageData = imageToBase64(fileName);

            MediaType mediaType = MediaType.parse("image/png");
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("image", fileName, RequestBody.create(imageData, mediaType))
                    .build();

            Request request = new Request.Builder()
                    .url(url)
                    .header("x-workspace-id", "ff58e155-45ad-4f29-ab72-63b1747ae982")
                    .addHeader("Authorization", Credentials.basic("wild_nature", "is_beautifull!"))
                    .post(requestBody)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) {
                    String responseBody = response.body().string();
                    log.error("Error response: {}", responseBody);
                }

                String responseBody = response.body().string();
                log.info("Response: {}", response);
                return Optional.ofNullable(mapToSuccessResponse(responseBody, ServerResponse.class));
            } catch (IOException e) {
                log.error("Cannot send image: {}", e.getMessage());
                throw new ImageException("Cannot send image");
            }
        }

        log.warn("Max attempts reached. Returning empty.");
        return Optional.empty();
    }

    byte[] imageToBase64(String fileName) {
        byte[] data;
        try {
            data = Files.readAllBytes(Paths.get(fileName));
        } catch (IOException e) {
            log.error("Cannot read image: {}", e.getMessage());
            throw new ImageException("Cannot read image");
        }
        return data;
    }

    private <T> T mapToSuccessResponse(String responseBody, Class<T> clazz) {
        try {
            return mapper.readValue(responseBody, clazz);
        } catch (IOException e) {
            log.error("Cannot map response: {}", e.getMessage());
            throw new ObjectMapperException("Cannot map response");
        }
    }

}
