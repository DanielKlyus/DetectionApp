package com.example.sber_ai.util;

import com.example.sber_ai.exception.ImageException;
import com.example.sber_ai.exception.ObjectMapperException;
import com.example.sber_ai.model.response.ServerResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@RequiredArgsConstructor
public class ImageSender {

    private final ObjectMapper objectMapper;

    private final OkHttpClient client = new OkHttpClient.Builder()
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

    public Optional<ServerResponse> sendImageToML(String fileName) {
        final int maxAttempts = 6;

        for (int attempt = 0; attempt < maxAttempts; ++attempt) {

            byte[] imageData = imageToBase64(fileName);

            MediaType mediaType = MediaType.parse("image/png");
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("image", fileName, RequestBody.create(imageData, mediaType))
                    .build();

            Request request = new Request.Builder()
                    .url(url)
                    .header("x-workspace-id", workspaceId)
                    .addHeader("Authorization", Credentials.basic(serverAuthUsername, serverAuthPassword))
                    .post(requestBody)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                String responseBody = response.body().string();
                if (!response.isSuccessful()) {
                    log.error("Error response: {}", responseBody);
                } else {
                    log.info("Response: {}", response);
                    return Optional.ofNullable(mapToSuccessResponse(responseBody, ServerResponse.class));
                }
            } catch (IOException e) {
                log.error("Cannot send image: {}", e.getMessage());
                throw new ImageException("Cannot send image");
            }
        }

        log.warn("Max attempts reached. Returning empty.");
        return Optional.empty();
    }

    private byte[] imageToBase64(String fileName) {
        final int maxWidth = 1333;
        final int maxHeight = 800;
        byte[] data;

        try {
            BufferedImage originalImage = ImageIO.read(Files.newInputStream(Paths.get(fileName)));

            int width = originalImage.getWidth();
            int height = originalImage.getHeight();
            float aspectRatio = (float) width / height;

            if (width > maxWidth) {
                width = maxWidth;
                height = (int) (maxWidth / aspectRatio);
            }

            if (height > maxHeight) {
                height = maxHeight;
                width = (int) (maxHeight * aspectRatio);
            }

            Image scaledImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = resizedImage.createGraphics();
            g2d.drawImage(scaledImage, 0, 0, null);
            g2d.dispose();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(resizedImage, "png", baos);
            baos.flush();
            data = baos.toByteArray();
            baos.close();
        } catch (IOException e) {
            log.error("Cannot read image: {}", e.getMessage());
            throw new ImageException("Cannot read image");
        }
        return data;
    }

    private <T> T mapToSuccessResponse(String responseBody, Class<T> clazz) {
        try {
            return objectMapper.readValue(responseBody, clazz);
        } catch (IOException e) {
            log.error("Cannot map response: {}", e.getMessage());
            throw new ObjectMapperException("Cannot map response");
        }
    }


}
