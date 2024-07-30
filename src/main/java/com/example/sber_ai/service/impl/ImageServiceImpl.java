package com.example.sber_ai.service.impl;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.example.sber_ai.exception.CategoryException;
import com.example.sber_ai.exception.ImageException;
import com.example.sber_ai.model.entity.Category;
import com.example.sber_ai.model.entity.Image;
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
import java.util.*;
import java.util.concurrent.TimeUnit;
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
        List<Image> images = new ArrayList<>();
        files.forEach(fileInfo -> {
            Image image = fileInfo.toEntity(fileInfo, categories);
            if (Objects.equals(image.getCategoryId().getClassType(), "animal") && image.getDateTime() != null) {
                images.add(image);
            } else {
                imageRepository.save(image);
                Category category = categoryRepository.findById(image.getCategoryId().getId()).orElseThrow(() -> new CategoryException("Category not found"));
                log.info("\nImage {} saved with category {}", image.getName(), category.getName());

            }
        });
        List<Image> updatedImages = countSeries(images);
        updatedImages.forEach(image -> {
            imageRepository.save(image);
            Category category = categoryRepository.findById(image.getCategoryId().getId()).orElseThrow(() -> new CategoryException("Category not found"));
            log.info("\nImage {} saved with category {}", image.getName(), category.getName());
        });
    }

    @Override
    public List<Image> countSeries(List<Image> images) {
        images.sort(new Comparator<Image>() {
            @Override
            public int compare(Image i1, Image i2) {
                return i1.getDateTime().compareTo(i2.getDateTime());
            }
        });
        int currentPassage = 0;
        int maxAnimals = 0;
        Stack<Image> stack = new Stack<>();
        for (Image image : images) {
            if (stack.isEmpty()) {
                // Начало проходов
                currentPassage++;
                image.setPassage(currentPassage);
                maxAnimals = image.getAnimalCount();
                stack.push(image);
            } else if (image.getCategoryId() != stack.peek().getCategoryId() ||
                    image.getDateTime().getTime() - stack.peek().getDateTime().getTime() > TimeUnit.MINUTES.toMillis(30)) {
                // Завершение текущего прохода
                while (!stack.isEmpty()) {
                    stack.pop().setAnimalCountInPassage(maxAnimals);
                }
                currentPassage++;
                maxAnimals = image.getAnimalCount();
                image.setPassage(currentPassage);
                stack.push(image);
            } else {
                // Продолжение текущего прохода
                image.setPassage(currentPassage);
                maxAnimals = Math.max(maxAnimals, image.getAnimalCount());
                stack.push(image);
            }
        }
        // Завершение последнего прохода
        while (!stack.isEmpty()) {
            stack.pop().setAnimalCountInPassage(maxAnimals);
        }
        return images;
    }

    protected List<FileInfo> processImages(String directoryPath) {
        List<FileInfo> files = convertSourceFileToFileInfo(directoryPath);

        files.forEach(fileInfo -> {
            Optional<ServerResponse> response = imageSender.sendImageToML(fileInfo.getPath());
            log.info("\n Server response: {}", response);
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

        try (Stream<Path> pathStream = Files.list(Path.of(directoryPath))) {
            pathStream
                    .filter(path -> {
                        String fileName = path.getFileName().toString().toLowerCase();
                        return fileName.endsWith(".jpeg") || fileName.endsWith(".png") || fileName.endsWith(".jpg");
                    })
                    .forEach(path -> {
                        File file = path.toFile();

                        int originalWidth;
                        int originalHeight;
                        Date dateTime;
                        try {
                            BufferedImage originalImage = ImageIO.read(file);
                            originalWidth = originalImage.getWidth();
                            originalHeight = originalImage.getHeight();
                        } catch (IOException e) {
                            throw new ImageException("Cannot read file: " + file.getPath());
                        }
                        try {
                            dateTime = extractDateTimeFromImage(new File(file.getPath()));
                        } catch (Exception e) {
                            throw new ImageException("Cannot extract date time from file: " + file.getPath());
                        }
                        imageDrawer.resizeImage(file);

                        try (FileInputStream inputStream = new FileInputStream(path.getParent() + "/temp/" + path.getFileName())) {
                            MockMultipartFile multipartFile = new MockMultipartFile(file.getName(), file.getName(), Files.probeContentType(path), inputStream);
                            FileInfo fileInfo = new FileInfo(file.getName(), path.getParent() + "/temp/" + path.getFileName(), multipartFile, dateTime, originalWidth, originalHeight);
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

    protected static Date extractDateTimeFromImage(File imageFile) throws Exception {
        Metadata metadata = ImageMetadataReader.readMetadata(imageFile);
        ExifSubIFDDirectory directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);

        if (directory != null) {
            return directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
        } else {
            return null;
        }
    }
}
