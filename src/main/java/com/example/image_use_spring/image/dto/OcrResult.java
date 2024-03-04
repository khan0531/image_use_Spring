package com.example.image_use_spring.image.dto;

import lombok.Data;

@Data
public class OcrResult {
  private String ocrDate;
  private Long ocrAmount;
  private String ocrVendor;
  private String ocrAddress;
}
