package com.example.image_use_spring.image.dto;

import com.example.image_use_spring.image.persist.entity.OcrResultEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OcrResult {
  private String ocrDate;
  private Long ocrAmount;
  private String ocrVendor;
  private String ocrAddress;

  public OcrResultEntity toEntity() {
    return OcrResultEntity.builder()
        .ocrDate(ocrDate)
        .ocrAmount(ocrAmount)
        .ocrVendor(ocrVendor)
        .ocrAddress(ocrAddress)
        .build();
  }
}
