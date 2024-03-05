package com.example.image_use_spring.image.util;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Component;

@Component
public class ImageUtil {

  public Path storeFile(byte[] fileData, Path path) throws IOException {
    Files.write(path, fileData);
    return path;
  }

  public byte[] compressImage(byte[] imageData) throws IOException {
    ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
    BufferedImage originalImage = ImageIO.read(bais); // 원본 이미지 읽기
    int originalWidth = originalImage.getWidth(); // 원본 너비
    int originalHeight = originalImage.getHeight(); // 원본 높이

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    Thumbnails.of(originalImage)
        .size(originalWidth, originalHeight) // 원본 이미지의 크기로 설정
        .outputQuality(0.75) // 출력 품질 설정 (0.0 ~ 1.0)
        .outputFormat("jpg") // 출력 형식 지정
        .toOutputStream(baos);

    bais.close(); // ByteArrayInputStream을 명시적으로 닫기
    return baos.toByteArray();
  }

  public byte[] createThumbnail(byte[] imageData, int width, int height) throws IOException {
    ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    Thumbnails.of(bais)
        .size(width, height) // 썸네일의 크기 지정
        .outputFormat("jpg") // 출력 형식 지정
        .toOutputStream(baos);

    return baos.toByteArray();
  }
}
