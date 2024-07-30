package com.example.sber_ai.util;

import com.example.sber_ai.exception.ImageException;
import com.example.sber_ai.model.response.ServerResponse;
import com.example.sber_ai.model.response.ServerResponse.MegadetectorPredict;
import com.example.sber_ai.model.response.ServerResponse.SpeciesPredict;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class ImageDrawer {

    public BufferedImage convertMultipartFileToBufferedImage(MultipartFile file) {
        try {
            return ImageIO.read(file.getInputStream());
        } catch (IOException e) {
            log.error("Cannot convert MultipartFile to BufferedImage: {}", e.getMessage());
            throw new ImageException("Cannot convert image");
        }
    }

    public MultipartFile convertBufferedImageToMultipartFile(String name, BufferedImage image) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", baos);
            return new MockMultipartFile(name, name, "image/png", baos.toByteArray());
        } catch (IOException e) {
            log.error("Cannot convert BufferedImage to MultipartFile: {}", e.getMessage());
            throw new ImageException("Cannot convert image");
        }
    }

    public BufferedImage drawMlResults(BufferedImage image, ServerResponse serverResponse) {
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(Color.RED);
        graphics.setStroke(new BasicStroke(10));

        SpeciesPredict speciesPredict = serverResponse.getSpeciesPredict();
        if (speciesPredict != null) {
            drawPredicts(graphics, speciesPredict.getBboxes());
        } else {
            log.error("Species predictions are null");
        }

        MegadetectorPredict megadetectorPredict = serverResponse.getMegadetectorPredict();
        if (megadetectorPredict != null) {
            drawPredicts(graphics, megadetectorPredict.getBboxes());
        } else {
            log.error("Megadetector predictions are null");
        }

        graphics.dispose();
        return image;
    }

    private void drawPredicts(Graphics2D graphics, List<List<Integer>> bboxes) {
        for (List<Integer> bbox : bboxes) {
            int x = bbox.get(0);
            int y = bbox.get(1);
            int width = bbox.get(2) - x;
            int height = bbox.get(3) - y;
            graphics.drawRect(x, y, width, height);
        }
    }

    public void resizeImage(File image) {
        final int maxWidth = 1333;
        final int maxHeight = 800;

        File tempDir = new File(image.getParentFile(), "temp");
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }

        File outputFile = new File(tempDir, image.getName());

        try {
            BufferedImage originalImage = ImageIO.read(image);

            int originalWidth = originalImage.getWidth();
            int originalHeight = originalImage.getHeight();

            if (originalWidth > maxWidth || originalHeight > maxHeight) {
                int newWidth = originalWidth;
                int newHeight = originalHeight;

                if (originalWidth > maxWidth) {
                    newWidth = maxWidth;
                }

                if (originalHeight > maxHeight) {
                    newHeight = maxHeight;
                }

                BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, originalImage.getType());
                Graphics2D g = resizedImage.createGraphics();
                g.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
                g.dispose();

                String imageFormat = image.getName().substring(image.getName().lastIndexOf(".") + 1);

                ImageIO.write(resizedImage, imageFormat, outputFile);
            }
        } catch (IOException e) {
            log.error("Cannot resize image {}: {}", image.getPath(), e.getMessage());
            throw new ImageException("Cannot resize image");
        }
    }

    public BufferedImage restoreImageSize(BufferedImage drawnImage, int targetWidth, int targetHeight) {
        BufferedImage restoredImage = new BufferedImage(targetWidth, targetHeight, drawnImage.getType());
        Graphics2D g = restoredImage.createGraphics();
        g.drawImage(drawnImage, 0, 0, targetWidth, targetHeight, null);
        g.dispose();
        return restoredImage;
    }
}
