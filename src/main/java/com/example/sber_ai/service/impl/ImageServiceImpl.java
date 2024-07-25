package com.example.sber_ai.service.impl;

import com.example.sber_ai.exception.ImageException;
import com.example.sber_ai.model.entity.Category;
import com.example.sber_ai.model.entity.Project;
import com.example.sber_ai.model.file.FileInfo;
import com.example.sber_ai.model.response.ServerResponse;
import com.example.sber_ai.repository.CategoryRepository;
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
    private final ProjectRepository projectRepository;

    private final ImageRepository imageRepository;

    private final CategoryRepository categoryRepository;

    private final ImageDrawer imageDrawer;

    private final ImageSender imageSender;

    private final MinioService minioService;

    private final ObjectMapper objectMapper;

    @Override
    public void uploadSourceFiles(String directoryPath, String projectName, String pathSave) {
        List<FileInfo> resultImages = processImages(directoryPath);
        minioService.uploadSourceFiles(resultImages, projectName);
        saveAll(resultImages, projectName);
    }

    @Override
    public String uploadFile(MultipartFile categoryImg) {
        try {
            return minioService.uploadFile(categoryImg);
        } catch (Exception e) {
            log.error("Cannot upload file: {}", e.getMessage());
            throw new ImageException("Cannot upload file");
        }
    }

    @Override
    public String getMinioCategoryUrl(String categoryType) {
        return minioService.getMinioCategoryUrl(categoryType);
    }

    @Override
    public void saveAll(List<FileInfo> files, String projectName) {
        Project project = projectRepository.findByName(projectName);
        List<Category> categories = categoryRepository.findAllByProjectId(project);
        files.forEach(fileInfo -> {
            imageRepository.save(fileInfo.toEntity(fileInfo, categories));
            log.info("Image {} saved with category {}", fileInfo.getName(), fileInfo.getCategory());
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

                double minMlThreshold = 0.3;
                ServerResponse.SpeciesPredict speciesPredict = response.get().getSpeciesPredict();
                ServerResponse.MegadetectorPredict megadetectorPredict = response.get().getMegadetectorPredict();
                if (!speciesPredict.getLabels().isEmpty()) {
                    log.info("Species predict labels for animal {} with file name {}: {}", fileInfo.getPath(), fileInfo.getName(), speciesPredict.getLabels());
                    fileInfo.setCategory(speciesPredict.getLabels().get(0));
                    fileInfo.setAnimalCount(speciesPredict.getLabels().size());
                    fileInfo.setThreshold(speciesPredict.getScores()
                            .stream()
                            .min(Double::compare)
                            .orElse(minMlThreshold));

                } else if (!megadetectorPredict.getLabels().isEmpty()) {
                    log.info("Megadetector predict labels for animal {} with file name {}: {}", fileInfo.getPath(), fileInfo.getName(), megadetectorPredict.getLabels());
                    fileInfo.setCategory(megadetectorPredict.getLabels().get(0));
                    fileInfo.setAnimalCount(megadetectorPredict.getLabels().size());
                    fileInfo.setThreshold(megadetectorPredict.getScores()
                            .stream()
                            .min(Double::compare)
                            .orElse(minMlThreshold));

                } else {
                    fileInfo.setCategory("empty");
                    fileInfo.setAnimalCount(0);
                    fileInfo.setThreshold(minMlThreshold);
                }
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

                        int originalWidth;
                        int originalHeight;
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
