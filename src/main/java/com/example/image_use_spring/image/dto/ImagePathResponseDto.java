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
public class ImagePathResponseDto {
  private String originalPath;
  private String compressedPath;
  private String thumbnailPath;
}
