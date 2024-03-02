package com.example.image_use_spring.image.service.impl;

import static com.example.image_use_spring.exception.type.ErrorCode.FILE_NOT_FOUND;
import static com.example.image_use_spring.exception.type.ErrorCode.INTERNAL_SERVER_ERROR;
import static com.example.image_use_spring.exception.type.ErrorCode.NOT_FOUND_TASK;

import com.example.image_use_spring.exception.ImageException;
import com.example.image_use_spring.image.dto.ImagePathResponseDto;
import com.example.image_use_spring.image.persist.entity.ImageEntity;
import com.example.image_use_spring.image.persist.entity.TaskStatus;
import com.example.image_use_spring.image.persist.repository.ImageRepository;
import com.example.image_use_spring.image.persist.repository.TaskStatusRepository;
import com.example.image_use_spring.image.service.ImageService;
import com.example.image_use_spring.image.util.ImageUtil;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

  private final Path imagesDirectory = Paths.get(System.getProperty("user.home"), "test-images");

  private static final int THUMBNAIL_WIDTH = 512;
  private static final int THUMBNAIL_HEIGHT = 512;

  private final ImageRepository imageRepository;
  private final TaskStatusRepository taskStatusRepository;
  private final ImageUtil imageUtil;

  @Override
  public ImagePathResponseDto getImagePath(String uuid) {
    ImageEntity image = imageRepository.findByUuid(uuid)
        .orElseThrow(() -> new ImageException(FILE_NOT_FOUND));

    return null;

  }

  @Override
  public String getTaskStatus(String uuid) {
    Optional<TaskStatus> taskStatus = taskStatusRepository.findByUuid(uuid).stream().findFirst();
    if (taskStatus != null) {
      return taskStatus.get().getStatus(); // 작업 상태 반환
    } else {
      log.error("Task 상태 확인 중에 발생");
     throw new ImageException(NOT_FOUND_TASK); // 작업이 존재하지 않는 경우
    }
  }

  private void saveTaskStatus(String uuid, String status) {
    TaskStatus taskStatus = new TaskStatus(uuid, status);
    taskStatusRepository.save(taskStatus);
  }

  @Override
  public CompletableFuture<Void> uploadFile(MultipartFile file, String callbackUrl, String checkStatus) {
    String uuid = UUID.randomUUID().toString().replace("-", "");
    byte[] imageBytes;
    try {
      imageBytes = file.getBytes();
    } catch (IOException e) {
      CompletableFuture<Void> failedFuture = new CompletableFuture<>();
      failedFuture.completeExceptionally(e);
      return failedFuture;
    }

    saveTaskStatus(checkStatus, "PROCESSING");

    CompletableFuture<ImageEntity> originalImageFuture = saveOriginalImage(imageBytes, checkStatus, uuid, FilenameUtils.getExtension(file.getOriginalFilename()));

    return originalImageFuture.thenCompose(originalImage -> {
      CompletableFuture<ImageEntity> compressedImageFuture = compressAndSaveImage(imageBytes, checkStatus, uuid, originalImage);
      CompletableFuture<ImageEntity> thumbnailImageFuture = createThumbnailAndSave(imageBytes, checkStatus, uuid, originalImage);
      return CompletableFuture.allOf(compressedImageFuture, thumbnailImageFuture).thenRunAsync(() -> {
        try {
          System.out.println("Image processing completed successfully2.");

          saveTaskStatus(checkStatus, "COMPLETED");
          notifyClient(callbackUrl, "Image processing completed successfully.", originalImageFuture.get(),compressedImageFuture.get(), thumbnailImageFuture.get());
        } catch (Exception e) {
          saveTaskStatus(checkStatus, "FAILED");
          log.error("Failed to notify client.", e);
          notifyClient(callbackUrl, "Image processing failed.", null, null, null);
        }
      });
    });
  }

  private void notifyClient(String callbackUrl, String message, ImageEntity originalImage, ImageEntity compressedImage, ImageEntity thumbnailImage) {
    // RestTemplate 인스턴스 생성
    RestTemplate restTemplate = new RestTemplate();

    // 클라이언트에 보낼 메시지 또는 데이터를 포함하는 객체 생성
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    String originalImageUrl = createImageUrl(originalImage.getImageFileName());
    String compressedImageUrl = createImageUrl(compressedImage.getImageFileName());
    String thumbnailImageUrl = createImageUrl(thumbnailImage.getImageFileName());

    Map<String, Object> body = new HashMap<>();
    body.put("message", message);
    body.put("originalImagePath", originalImageUrl);
    body.put("compressedImagePath", compressedImageUrl);
    body.put("thumbnailImagePath", thumbnailImageUrl);

    HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

    // HTTP POST 요청을 콜백 URL에 전송
    ResponseEntity<String> response = restTemplate.postForEntity(callbackUrl, requestEntity, String.class);

    // 클라이언트에서 어떻게 수신하는지 로깅
    log.info("Request body: " + body);

    log.info("Callback response: " + response.getStatusCode());
    log.info("Callback response body: " + response.getBody());
    log.info("Callback response headers: " + response.getHeaders());
  }

  // 이미지 파일 이름을 이용하여 외부에서 접근 가능한 URL 생성
  private String createImageUrl(String imageFileName) {
    return "https://localhost:8080/images/" + imageFileName;
  }

  private CompletableFuture<ImageEntity> saveOriginalImage(byte[] imageBytes, String callbackUrl, String uuid, String extension) {

    return CompletableFuture.supplyAsync(() -> {
      try {
        String originalFileName = uuid + "." + extension;
        Path originalFilePath = imageUtil.storeFile(imageBytes,
            imagesDirectory.resolve("originals").resolve(originalFileName));
        return saveImageEntity(callbackUrl, originalFilePath, originalFileName, null);
      } catch (Exception e) {
        log.error("Failed to save original image.", e);
        throw new ImageException(INTERNAL_SERVER_ERROR);
      }
    });
  }

  private CompletableFuture<ImageEntity> compressAndSaveImage(byte[] imageBytes, String callbackUrl, String uuid, ImageEntity originalImage) {
    return CompletableFuture.supplyAsync(() -> {
      try {
        byte[] compressedImage = imageUtil.compressImage(imageBytes);
        String compressedFileName = "comp-" + uuid + "." + "jpg";
        Path compressedFilePath = imageUtil.storeFile(compressedImage,
            imagesDirectory.resolve("compresses").resolve(compressedFileName));
        return saveImageEntity(callbackUrl, compressedFilePath, compressedFileName, originalImage);
      } catch (Exception e) {
        log.error("Failed to compress and save image.", e);
        throw new ImageException(INTERNAL_SERVER_ERROR);
      }
    });
  }

  private CompletableFuture<ImageEntity> createThumbnailAndSave(byte[] imageBytes, String callbackUrl, String uuid, ImageEntity originalImage) {
    return CompletableFuture.supplyAsync(() -> {
      try {
        byte[] thumbnailImage = imageUtil.createThumbnail(imageBytes, THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT);
        String thumbnailFileName = "thumb-" + uuid + "." + "jpg";
        Path thumbnailFilePath = imageUtil.storeFile(thumbnailImage,
            imagesDirectory.resolve("thumbnails").resolve(thumbnailFileName));
        return saveImageEntity(callbackUrl, thumbnailFilePath, thumbnailFileName, originalImage);
      } catch (Exception e) {
        log.error("Failed to create thumbnail and save image.", e);
        throw new ImageException(INTERNAL_SERVER_ERROR);
      }
    });
  }

  private ImageEntity saveImageEntity(String callbackUrl, Path filePath, String fileName, ImageEntity originalImage) {
    return imageRepository.save(ImageEntity.builder()
        .uuid(callbackUrl)
        .imagePath(filePath.toString())
        .imageFileName(fileName)
        .originalImage(originalImage)
        .build());
  }

  @Override
  public Resource loadImageAsResource(String imageFileName) {
    ImageEntity image = imageRepository.findByImageFileName(imageFileName)
        .orElseThrow(() -> new ImageException(FILE_NOT_FOUND));

    String imagePath = image.getImagePath();

    Resource resource = new FileSystemResource(imagePath);

    if (resource.exists() || resource.isReadable()) {
      return resource;
    } else {
      throw new ImageException(INTERNAL_SERVER_ERROR);
    }
  }
}
