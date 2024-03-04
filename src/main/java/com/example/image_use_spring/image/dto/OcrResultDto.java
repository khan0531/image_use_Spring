package com.example.image_use_spring.image.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OcrResultDto {
  private String date;
  private Long amount;
  private String vendor;
  private String address;
}
