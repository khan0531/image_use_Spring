package com.example.image_use_spring.image.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImageResponseDto {
  private Long imageId;
  private String originalFilePath;
  private String compressedFilePath;
  private String thumbnailFilePath;
}
