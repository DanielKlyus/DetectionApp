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

import javax.imageio.ImageIO;
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
    public void uploadSourceFiles(String directoryPath, String projectName, String pathSave) {
        List<FileInfo> resultImages = processImages(directoryPath);
        minioService.upload(resultImages, projectName);
        saveAll(resultImages, projectName);
    }

    @Override
    public void saveAll(List<FileInfo> files, String projectName) {
        Project projectId = projectRepository.findByName(projectName).orElseThrow(() -> new ImageException("Project not found"));
        files.forEach(fileInfo -> {
            try {
                imageRepository.save(fileInfo.toEntity(fileInfo, projectId));
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

                BufferedImage restoredImage = imageDrawer.restoreImageSize(drawnImage, fileInfo.getOriginalWidth(), fileInfo.getOriginalHeight());

                MultipartFile drawnImageFile = imageDrawer.convertBufferedImageToMultipartFile(fileInfo.getName(), restoredImage);
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

                        int originalWidth = 0;
                        int originalHeight = 0;
                        try {
                            BufferedImage originalImage = ImageIO.read(file);
                            originalWidth = originalImage.getWidth();
                            originalHeight = originalImage.getHeight();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        imageDrawer.resizeImage(file);

                        try (FileInputStream inputStream = new FileInputStream(file)) {
                            MockMultipartFile multipartFile = new MockMultipartFile(file.getName(), file.getName(), Files.probeContentType(path), inputStream);
                            FileInfo fileInfo = new FileInfo(file.getName(), path.toString(), multipartFile, originalWidth, originalHeight);
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
