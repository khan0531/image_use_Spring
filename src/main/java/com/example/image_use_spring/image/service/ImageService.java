package com.example.image_use_spring.image.service;

import com.example.image_use_spring.image.dto.ImageResponseDto;
import java.io.IOException;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {

  ImageResponseDto uploadFile(MultipartFile file) throws Exception;

  Resource loadImageAsResource(String imageFileName);
}
