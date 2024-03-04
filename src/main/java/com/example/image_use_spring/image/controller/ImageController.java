package com.example.image_use_spring.image.controller;

import com.example.image_use_spring.image.dto.ImagePathResponseDto;
import com.example.image_use_spring.image.dto.OcrResultDto;
import com.example.image_use_spring.image.service.ImageService;
import com.example.image_use_spring.member.domain.Member;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
public class ImageController {

  private final ImageService imageService;
  private final ObjectMapper objectMapper;

  @PostMapping("/upload")
  public ResponseEntity<?> uploadFile(
      @RequestParam("file") MultipartFile file,
      @RequestPart("json-data") String jsonData,
      @RequestParam("callbackUrl") String callbackUrl,
      @AuthenticationPrincipal Member member
  ) throws IOException {
    String checkStatus = UUID.randomUUID().toString().replace("-", "");

    JsonNode rootNode = objectMapper.readTree(jsonData);
    boolean isReceipt = rootNode.get("isReceipt").asBoolean();

    // 비동기 작업 시작
    imageService.uploadFile(file, callbackUrl, checkStatus, member, isReceipt);

    // 비동기 작업이 정상적으로 시작 되었다는 응답, 비동기 작업을 추적할 수 있도록 UUID 반환
    Map<String, String> response = new HashMap<>();
    response.put("message", "Image processing started");
    response.put("checkStatus Code", checkStatus);

    return ResponseEntity.accepted().body(response);
  }

  @GetMapping("/status/{statusCode}")
  public ResponseEntity<?> getStatus(@PathVariable String statusCode) {
    // UUID로 만든 status 코드를 사용하여 작업 상태를 조회
    String status = imageService.getTaskStatus(statusCode);
    return ResponseEntity.ok().body(status);
  }

  @PostMapping("/callback")
  public ResponseEntity<String> handleCallback(@RequestBody Map<String, Object> callbackData) {
    return ResponseEntity.ok("");
  }

  @GetMapping("/{imageFileName}")
  public ResponseEntity<Resource> getImageUrl(
      @PathVariable String imageFileName,
      @AuthenticationPrincipal Member member) {

    Resource resource = imageService.loadImageAsResource(imageFileName, member);
    return ResponseEntity.ok()
        .contentType(MediaType.IMAGE_JPEG)
        .body(resource);
  }

  @GetMapping("/ocr/{id}")
  public ResponseEntity<OcrResultDto> getOcrResult(@PathVariable Long id) {
    OcrResultDto response = imageService.getOcrResult(id);
    return ResponseEntity.ok().body(response);
  }
}
