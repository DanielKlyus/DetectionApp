package com.example.sber_ai.service.impl;

import com.example.sber_ai.exception.ImageException;
import com.example.sber_ai.model.file.FileInfo;
import com.example.sber_ai.service.MinioService;
import com.example.sber_ai.util.ImageDrawer;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@SpringBootTest
@Slf4j
class ImageServiceImplTest {

    private final String url = "https://mlspace.ai.cloud.ru/deployments/dgx2-inf-001/kfserving-1718613717/species_detection/find_and_classify";

    private final String workspaceId = "ff58e155-45ad-4f29-ab72-63b1747ae982";

    private final String serverAuthUsername = "wild_nature";

    private final String serverAuthPassword = "is_beautifull!";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MinioService minioService;

    @Autowired
    private ImageDrawer imageDrawer;

    @Autowired
    private ImageServiceImpl imageService;

//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//
//        imageService.setUrl(url);
//        imageService.setWorkspaceId(workspaceId);
//        imageService.setServerAuthUsername(serverAuthUsername);
//        imageService.setServerAuthPassword(serverAuthPassword);
//    }
//
//    @Test
//    void sendImageToMLTest() {
//        String fileName = "src/test/resources/pictures/gimalayBear.png";
//
//        Optional<ServerResponse> result = imageService.sendImageToML(fileName);
//
//        Assertions.assertNotNull(result);
//        Assertions.assertTrue(result.isPresent());
//    }

    @Test
    void convertSourceFileToFileInfoTest() {
        String testDirectoryPath = "src/test/resources/testfiles";

        createTestFiles(testDirectoryPath);

        List<FileInfo> fileInfoList = imageService.convertSourceFileToFileInfo(testDirectoryPath);

        Assertions.assertNotNull(fileInfoList);
        Assertions.assertFalse(fileInfoList.isEmpty());
        fileInfoList.forEach(fileInfo -> {
            try (InputStream fileInfoStream = fileInfo.getFile().getInputStream()) {
                byte[] fileInfoBytes = fileInfoStream.readAllBytes();

                File testFile = new File(testDirectoryPath + "/" + fileInfo.getName());
                byte[] testFileBytes = Files.readAllBytes(testFile.toPath());

                Assertions.assertArrayEquals(testFileBytes, fileInfoBytes);

            } catch (IOException e) {
                throw new ImageException("Error comparing file contents");
            }
        });

        deleteTestFiles(testDirectoryPath);
    }

    private void createTestFiles(String directoryPath) {
        try {
            Files.createDirectories(Path.of(directoryPath));

            File file1 = new File(directoryPath + "/test1.txt");
            try (FileOutputStream fos = new FileOutputStream(file1)) {
                fos.write("Test 1.".getBytes());
            }

            File file2 = new File(directoryPath + "/test2.txt");
            try (FileOutputStream fos = new FileOutputStream(file2)) {
                fos.write("Test 2.".getBytes());
            }
        } catch (IOException e) {
            throw new ImageException("Cannot create test files");
        }
    }

    private void deleteTestFiles(String directoryPath) {
        try (Stream<Path> pathStream = Files.walk(Path.of(directoryPath))) {
            pathStream
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(file -> {
                        if (!file.delete()) {
                            log.error("Failed to delete file: {}", file.getAbsolutePath());
                        }
                    });
        } catch (IOException e) {
            throw new ImageException("Cannot delete test files");
        }
    }
}