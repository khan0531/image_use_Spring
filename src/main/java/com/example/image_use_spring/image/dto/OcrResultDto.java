package com.example.image_use_spring.image.dto;

import com.example.image_use_spring.image.persist.entity.OcrResultEntity;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OcrResultDto {
  private String ocrDate;
  private Long ocrAmount;
  private String ocrVendor;
  private String ocrAddress;

  public static OcrResultDto fromEntity(OcrResultEntity ocrResultEntity) {
    return OcrResultDto.builder()
        .ocrDate(ocrResultEntity.getOcrDate())
        .ocrAmount(ocrResultEntity.getOcrAmount())
        .ocrVendor(ocrResultEntity.getOcrVendor())
        .ocrAddress(ocrResultEntity.getOcrAddress())
        .build();
  }
}
