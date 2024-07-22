package com.example.sber_ai.util;

import com.example.sber_ai.exception.ImageException;
import com.example.sber_ai.model.response.ServerResponse;
import com.example.sber_ai.model.response.ServerResponse.SpeciesPredict;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
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

        SpeciesPredict predict = serverResponse.getSpeciesPredict();
        if (predict != null) {
            for (List<Integer> bbox : predict.getBboxes()) {
                int x = bbox.get(0);
                int y = bbox.get(1);
                int width = bbox.get(2) - x;
                int height = bbox.get(3) - y;
                graphics.drawRect(x, y, width, height);
            }
        } else {
            log.error("Predictions are null");
        }

        graphics.dispose();
        return image;
    }
}
