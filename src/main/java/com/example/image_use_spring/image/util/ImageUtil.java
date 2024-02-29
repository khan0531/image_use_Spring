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
import org.springframework.stereotype.Component;

@Component
public class ImageUtil {

  public Path storeFile(byte[] fileData, Path path) throws IOException {
    Files.write(path, fileData);
    return path;
  }

  public byte[] compressImage(byte[] imageData) throws IOException {
    ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
    BufferedImage image = ImageIO.read(bais);

    // RGB 컬러 모델로 이미지 변환
    BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
    Graphics2D g = newImage.createGraphics();
    g.drawImage(image, 0, 0, null);
    g.dispose();

    // JPEG 이미지 압축 품질 설정
    float quality = 0.75f;
    ImageWriter jpgWriter = ImageIO.getImageWritersByFormatName("jpg").next();
    ImageWriteParam jpgWriteParam = jpgWriter.getDefaultWriteParam();
    jpgWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
    jpgWriteParam.setCompressionQuality(quality);

    ByteArrayOutputStream compressed = new ByteArrayOutputStream();
    ImageOutputStream ios = ImageIO.createImageOutputStream(compressed);
    jpgWriter.setOutput(ios);
    jpgWriter.write(null, new IIOImage(newImage, null, null), jpgWriteParam);

    jpgWriter.dispose();
    ios.close();
    bais.close();
    compressed.close();

    return compressed.toByteArray();
  }


  public byte[] createThumbnail(byte[] imageData, int width, int height) throws IOException {
    ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
    BufferedImage image = ImageIO.read(bais);

    // 원본 이미지 비율 계산
    double ratio = (double) image.getWidth() / image.getHeight();
    int thumbnailWidth = width;
    int thumbnailHeight = height;

    // 너비 또는 높이 중에서 큰 쪽에 맞추어 비율 조정
    if (width / height > ratio) {
      thumbnailWidth = (int) (height * ratio);
    } else {
      thumbnailHeight = (int) (width / ratio);
    }

    BufferedImage thumbnailImage = new BufferedImage(thumbnailWidth, thumbnailHeight, BufferedImage.TYPE_INT_RGB);
    Graphics2D graphics2D = thumbnailImage.createGraphics();

    graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

    // 이미지 크기 조정
    graphics2D.drawImage(image, 0, 0, thumbnailWidth, thumbnailHeight, null);
    graphics2D.dispose();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ImageIO.write(thumbnailImage, "jpg", baos);
    byte[] thumbnailData = baos.toByteArray();

    bais.close();
    baos.close();

    return thumbnailData;
  }

}
