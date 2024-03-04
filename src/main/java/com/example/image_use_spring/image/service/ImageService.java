package com.example.image_use_spring.image.service;

import com.example.image_use_spring.image.dto.ImagePathResponseDto;
import com.example.image_use_spring.image.dto.ImageResponseDto;
import com.example.image_use_spring.image.dto.OcrResultDto;
import com.example.image_use_spring.member.domain.Member;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {

  CompletableFuture<Void> uploadFile(MultipartFile file, String callbackUrl, String checkStatus, Member member, boolean isReceipt) throws IOException;

  String getTaskStatus(String uuid);

  Resource loadImageAsResource(String imageFileName, Member member);

  ImagePathResponseDto getImagePath(String uuid);

  OcrResultDto getOcrResult(Long id);
}
