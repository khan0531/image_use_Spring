package com.example.image_use_spring.image.service.impl;

import static com.example.image_use_spring.exception.type.ErrorCode.FILE_NOT_FOUND;
import static com.example.image_use_spring.exception.type.ErrorCode.INTERNAL_SERVER_ERROR;
import static com.example.image_use_spring.exception.type.ErrorCode.NOT_VALID_FILE_TYPE;

import com.example.image_use_spring.exception.ImageException;
import com.example.image_use_spring.image.dto.ImageResponseDto;
import com.example.image_use_spring.image.persist.entity.ImageEntity;
import com.example.image_use_spring.image.persist.repository.ImageRepository;
import com.example.image_use_spring.image.service.ImageService;
import com.example.image_use_spring.image.util.ImageUtil;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.hibernate.type.ImageType;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

  private final String homeDirectory = System.getProperty("user.home");
  private final Path imagesDirectory = Paths.get(homeDirectory, "asts-images");

  private static final int THUMBNAIL_WIDTH = 512;
  private static final int THUMBNAIL_HEIGHT = 512;

  private final ImageRepository imageRepository;
  private final ImageUtil imageUtil;

  @Override
  public ImageResponseDto uploadFile(MultipartFile file) throws Exception {
    if (!file.getContentType().startsWith("image")) {
      throw new ImageException(NOT_VALID_FILE_TYPE);
    }

    // 파일 확장자 추출
    String extension = FilenameUtils.getExtension(file.getOriginalFilename());

    // 파일 이름을 위한 UUID 생성
    String uuid = UUID.randomUUID().toString().replace("-", "");

    // 원본 파일 이름과 경로 설정, 저장
    String originalFileName = uuid + "." + extension;
    Path originalFilePath = imageUtil.storeFile(file.getBytes(),
        imagesDirectory.resolve("originals").resolve(originalFileName));
    ImageEntity originalImage = saveImageEntity(originalFilePath, originalFileName, null);

    // 압축 이미지 생성 및 저장
    byte[] compressedImage = imageUtil.compressImage(file.getBytes());
    String compressedFileName = "comp-" + uuid + "." + "jpg";
    Path compressedFilePath = imageUtil.storeFile(compressedImage,
        imagesDirectory.resolve("compresses").resolve(compressedFileName));
    ImageEntity compressed = saveImageEntity(compressedFilePath, compressedFileName, originalImage);

    // 썸네일 이미지 생성 및 저장
    byte[] thumbnailImage = imageUtil.createThumbnail(file.getBytes(), THUMBNAIL_WIDTH,
        THUMBNAIL_HEIGHT);
    String thumbnailFileName = "thumb-" + uuid + "." + "jpg";
    Path thumbnailFilePath = imageUtil.storeFile(thumbnailImage,
        imagesDirectory.resolve("thumbnails").resolve(thumbnailFileName));
    ImageEntity thumbnail = saveImageEntity(thumbnailFilePath, thumbnailFileName, originalImage);

    return ImageResponseDto.builder()
        .imageId(originalImage.getId())
        .originalFilePath(originalImage.getImagePath())
        .compressedFilePath(compressed.getImagePath())
        .thumbnailFilePath(thumbnail.getImagePath())
        .build();
  }

  private ImageEntity saveImageEntity(Path filePath, String fileName, ImageEntity originalImage) {
    return imageRepository.save(ImageEntity.builder()
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
