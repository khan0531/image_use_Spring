package com.example.image_use_spring;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ImageUseSpringApplication implements CommandLineRunner {
  private final String homeDirectory = System.getProperty("user.home");
  private final Path imagesDirectory = Paths.get(homeDirectory, "test-images");

  public static void main(String[] args) {
    SpringApplication.run(ImageUseSpringApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    initializeDirectories();
  }

  private void initializeDirectories() throws IOException {
    // 필요한 디렉토리 경로 정의
    Path originalsDir = imagesDirectory.resolve("originals");
    Path compressesDir = imagesDirectory.resolve("compresses");
    Path thumbnailsDir = imagesDirectory.resolve("thumbnails");

    // 디렉토리 생성
    Files.createDirectories(originalsDir);
    Files.createDirectories(compressesDir);
    Files.createDirectories(thumbnailsDir);
  }
}
