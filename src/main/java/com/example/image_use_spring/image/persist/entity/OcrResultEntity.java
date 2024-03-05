package com.example.image_use_spring.image.persist.entity;

import com.example.image_use_spring.common.entity.BaseTimeEntity;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ocr_result")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OcrResultEntity extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private LocalDateTime ocrDate;
  private float ocrAmount;
  private String ocrVendor;
  private String ocrAddress;
}
