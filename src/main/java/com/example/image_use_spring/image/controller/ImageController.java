package com.example.image_use_spring.image.controller;

import com.example.image_use_spring.image.dto.ImageResponseDto;
import com.example.image_use_spring.image.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class ImageController {

  private final ImageService imageService;

  @PostMapping("/upload")
  public ResponseEntity<ImageResponseDto> uploadFile(@RequestParam("file") MultipartFile file) throws Exception {

    ImageResponseDto responseMessage = imageService.uploadFile(file);

    return ResponseEntity.ok().body(responseMessage);
  }

  @GetMapping("/{imageFileName}")
  public ResponseEntity<Resource> getImageUrl(@PathVariable String imageFileName) {

    Resource resource = imageService.loadImageAsResource(imageFileName);
    String contentType = "image/jpeg";

    return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType(contentType))
        .body(resource);
  }
}
