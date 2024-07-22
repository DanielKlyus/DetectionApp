package com.example.sber_ai.service.impl;

import com.example.sber_ai.exception.ImageException;
import com.example.sber_ai.model.entity.Project;
import com.example.sber_ai.model.file.FileInfo;
import com.example.sber_ai.model.response.ServerResponse;
import com.example.sber_ai.repository.ImageRepository;
import com.example.sber_ai.repository.ProjectRepository;
import com.example.sber_ai.service.ImageService;
import com.example.sber_ai.service.MinioService;
import com.example.sber_ai.util.ImageDrawer;
import com.example.sber_ai.util.ImageSender;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
@Setter
@Getter
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;

    private final ProjectRepository projectRepository;

    private final ImageDrawer imageDrawer;

    private final ImageSender imageSender;

    private final MinioService minioService;

    private final ObjectMapper objectMapper;

    @Override
    public List<FileInfo> uploadSourceFiles(String directoryPath, String projectName, String pathSave) {
        List<FileInfo> resultImages = processImages(directoryPath);
        minioService.upload(resultImages, projectName);
        saveAll(resultImages, projectName);
        log.info("Uploaded {} images", resultImages);
        return resultImages;
    }

    @Override
    public void saveAll(List<FileInfo> files, String projectName) {
        Project projectId = projectRepository.findByName(projectName).orElseThrow(() -> new ImageException("Project not found"));
        files.forEach(fileInfo -> {
            try {
                imageRepository.save(fileInfo.toEntity(fileInfo, projectId));
                log.info("Saved image {}", fileInfo);
            } catch (ImageException e) {
                log.error("Cannot save image: {}", e.getMessage());
            }
        });
    }

    protected List<FileInfo> processImages(String directoryPath) {
        List<FileInfo> files = convertSourceFileToFileInfo(directoryPath);

        files.forEach(fileInfo -> {
            Optional<ServerResponse> response = imageSender.sendImageToML(fileInfo.getPath());

            response.ifPresent(serverResponse -> {
                BufferedImage image = imageDrawer.convertMultipartFileToBufferedImage(fileInfo.getFile());
                BufferedImage drawnImage = imageDrawer.drawMlResults(image, serverResponse);

                MultipartFile drawnImageFile = imageDrawer.convertBufferedImageToMultipartFile(fileInfo.getName(), drawnImage);
                fileInfo.setFile(drawnImageFile);
            });
        });

        return files;
    }

    protected List<FileInfo> convertSourceFileToFileInfo(String directoryPath) {
        List<FileInfo> result = new ArrayList<>();

        try (Stream<Path> pathStream = Files.walk(Path.of(directoryPath))) {
            pathStream
                    .filter(Files::isRegularFile)
                    .forEach(path -> {
                        File file = path.toFile();

                        try (FileInputStream inputStream = new FileInputStream(file)) {
                            MockMultipartFile multipartFile = new MockMultipartFile(file.getName(), file.getName(), Files.probeContentType(path), inputStream);
                            FileInfo fileInfo = new FileInfo(file.getName(), path.toString(), multipartFile);
                            result.add(fileInfo);
                        } catch (IOException e) {
                            log.error("Cannot upload images from path {} : {}", directoryPath, e.getMessage());
                            throw new ImageException("Cannot upload images");
                        }
                    });

        } catch (IOException e) {
            log.error("Cannot upload images from path {} : {}", directoryPath, e.getMessage());
            throw new ImageException("Cannot upload images");
        }

        return result;
    }

}
