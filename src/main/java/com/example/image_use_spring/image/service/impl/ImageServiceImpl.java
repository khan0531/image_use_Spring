package com.example.image_use_spring.image.service.impl;

import static com.example.image_use_spring.exception.type.ErrorCode.FILE_NOT_FOUND;
import static com.example.image_use_spring.exception.type.ErrorCode.INTERNAL_SERVER_ERROR;
import static com.example.image_use_spring.exception.type.ErrorCode.NOT_FOUND_TASK;
import static com.example.image_use_spring.exception.type.ErrorCode.OCR_FAILED;
import static com.example.image_use_spring.exception.type.ErrorCode.OCR_NOT_FOUND;

import com.example.image_use_spring.exception.ImageException;
import com.example.image_use_spring.image.dto.ImagePathResponseDto;
import com.example.image_use_spring.image.dto.OcrResult;
import com.example.image_use_spring.image.dto.OcrResultDto;
import com.example.image_use_spring.image.persist.entity.ImageEntity;
import com.example.image_use_spring.image.persist.entity.TaskStatus;
import com.example.image_use_spring.image.persist.repository.ImageRepository;
import com.example.image_use_spring.image.persist.repository.OcrResultRepository;
import com.example.image_use_spring.image.persist.repository.TaskStatusRepository;
import com.example.image_use_spring.image.service.ImageService;
import com.example.image_use_spring.image.util.ImageUtil;
import com.example.image_use_spring.image.util.OcrUtil;
import com.example.image_use_spring.member.domain.Member;
import com.example.image_use_spring.member.service.MemberService;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

  @Value("${cloud.aws.s3.bucket-name}")
  private String bucketName;

  @Value("${cloud.aws.cloudfront.domain-name}")
  private String cloudFrontDomainName;

  private final S3Client s3Client;

  private static final int THUMBNAIL_WIDTH = 512;
  private static final int THUMBNAIL_HEIGHT = 512;

  private final MemberService memberService;

  private final ImageRepository imageRepository;
  private final TaskStatusRepository taskStatusRepository;
  private final OcrResultRepository ocrResultRepository;

  private final ImageUtil imageUtil;
  private final OcrUtil ocrUtil;

  @Override
  public OcrResultDto getOcrResult(Long id) {
    return ocrResultRepository.findById(id)
        .map(OcrResultDto::fromEntity)
        .orElseThrow(() -> new ImageException(OCR_NOT_FOUND));
  }

  @Override
  public String getImageUrl(String imageFileName, Member member) {
    String imagePath = imageRepository.findByImageFileName(imageFileName)
        .orElseThrow(() -> new ImageException(FILE_NOT_FOUND))
        .getImagePath();
    return imagePath;
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

//  @Override
//  public Resource loadImageAsResource(String imageFileName, Member member) {
//    ImageEntity image = imageRepository.findByImageFileName(imageFileName)
//        .orElseThrow(() -> new ImageException(FILE_NOT_FOUND));
//
//    memberService.validateAndGetMember(image.getMember().getId(), member);
//
//    String imagePath = image.getImagePath();
//
//    Resource resource = new FileSystemResource(imagePath);
//
//    if (resource.exists() || resource.isReadable()) {
//      return resource;
//    } else {
//      throw new ImageException(FILE_NOT_FOUND);
//    }
//  }

  @Override
  public CompletableFuture<Void> uploadFile(MultipartFile file, String callbackUrl, String checkStatus, Member member) {
    memberService.validateAndGetMember(member);

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

    CompletableFuture<ImageEntity> originalImageFuture = saveOriginalImage(imageBytes, checkStatus, uuid, FilenameUtils.getExtension(file.getOriginalFilename()), member);

    return originalImageFuture.thenCompose(originalImage -> {
      CompletableFuture<ImageEntity> compressedImageFuture = compressAndSaveImage(imageBytes, checkStatus, uuid, originalImage, member);
      CompletableFuture<ImageEntity> thumbnailImageFuture = createThumbnailAndSave(imageBytes, checkStatus, uuid, originalImage, member);
      return CompletableFuture.allOf(compressedImageFuture, thumbnailImageFuture).thenRunAsync(() -> {
        try {
          System.out.println("Image processing completed successfully.");

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

  public void performOcrAnalysisAsync(MultipartFile file) throws Exception {

    OcrResult ocrResult = ocrUtil.analyzeReceipt(file);

    ocrResultRepository.save(ocrResult.toEntity());
  }

  private void notifyClient(String callbackUrl, String message, ImageEntity originalImage, ImageEntity compressedImage, ImageEntity thumbnailImage) {
    // RestTemplate 인스턴스 생성
    RestTemplate restTemplate = new RestTemplate();

    // 클라이언트에 보낼 메시지 또는 데이터를 포함하는 객체 생성
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    Map<String, Object> body = new HashMap<>();
    body.put("message", message);
    body.put("originalImagePath", originalImage.getImagePath());
    body.put("compressedImagePath", compressedImage.getImagePath());
    body.put("thumbnailImagePath", thumbnailImage.getImagePath());

    HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

    // HTTP POST 요청을 콜백 URL에 전송
    ResponseEntity<String> response = restTemplate.postForEntity(callbackUrl, requestEntity, String.class);

    // 클라이언트에서 어떻게 수신하는지 로깅
    log.info("Request body: " + body);
    log.info("Callback response: " + response.getStatusCode());
    log.info("Callback response body: " + response.getBody());
    log.info("Callback response headers: " + response.getHeaders());
  }

  private CompletableFuture<ImageEntity> saveOriginalImage(byte[] imageBytes, String callbackUrl, String uuid, String extension,  Member member) {

    return CompletableFuture.supplyAsync(() -> {
      try {
        String originalFileName = uuid + "." + extension;
        String originalFileKey = "originals/" + originalFileName;
        uploadFileToS3(imageBytes, originalFileKey, "image/" + extension);
        return saveImageEntity(callbackUrl, getFileUrl(originalFileKey), originalFileName, null, member);
      } catch (Exception e) {
        log.error("Failed to save original image.", e.getMessage());
        throw new ImageException(INTERNAL_SERVER_ERROR);
      }
    });
  }

  private String getFileUrl(String fileKey) {
    return "https://" + cloudFrontDomainName + "/" + fileKey;
  }

//  private String getFileUrl(String fileKey) {
//    return s3Client.utilities().getUrl(builder -> builder.bucket(bucketName).key(fileKey)).toExternalForm();
//  }

  private void uploadFileToS3(byte[] fileData, String fileKey, String mimeType) {
    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
        .bucket(bucketName)
        .key(fileKey)
        .contentType(mimeType)
        .build();

    s3Client.putObject(putObjectRequest, RequestBody.fromBytes(fileData));
  }

  private CompletableFuture<ImageEntity> compressAndSaveImage(byte[] imageBytes, String callbackUrl, String uuid, ImageEntity originalImage,  Member member) {
    return CompletableFuture.supplyAsync(() -> {
      try {
        byte[] compressedImage = imageUtil.compressImage(imageBytes);
        String compressedFileName = "comp-" + uuid + "." + "jpg";
        String compressedFileKey = "compresses/" + compressedFileName;
        uploadFileToS3(compressedImage, compressedFileKey, "image/jpeg");
        return saveImageEntity(callbackUrl, getFileUrl(compressedFileKey), compressedFileName, originalImage, member);
      } catch (Exception e) {
        log.error("Failed to compress and save image.", e.getMessage());
        throw new ImageException(INTERNAL_SERVER_ERROR);
      }
    });
  }

  private CompletableFuture<ImageEntity> createThumbnailAndSave(byte[] imageBytes, String callbackUrl, String uuid, ImageEntity originalImage,  Member member) {
    return CompletableFuture.supplyAsync(() -> {
      try {
        byte[] thumbnailImage = imageUtil.createThumbnail(imageBytes, THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT);
        String thumbnailFileName = "thumb-" + uuid + "." + "jpg";
        String thumbnailFileKey = "thumbnails/" + thumbnailFileName;
        uploadFileToS3(thumbnailImage, thumbnailFileKey, "image/jpeg");
        return saveImageEntity(callbackUrl, getFileUrl(thumbnailFileKey), thumbnailFileName, originalImage, member);
      } catch (Exception e) {
        log.error("Failed to create thumbnail and save image.", e.getMessage());
        throw new ImageException(INTERNAL_SERVER_ERROR);
      }
    });
  }

  private ImageEntity saveImageEntity(String callbackUrl, String filePath, String fileName, ImageEntity originalImage, Member member) {
    return imageRepository.save(ImageEntity.builder()
        .uuid(callbackUrl)
        .imagePath(filePath)
        .imageFileName(fileName)
        .member(member.toEntity())
        .originalImage(originalImage)
        .build());
  }

  private void saveTaskStatus(String uuid, String status) {
    TaskStatus taskStatus = new TaskStatus(uuid, status);
    taskStatusRepository.save(taskStatus);
  }

}
