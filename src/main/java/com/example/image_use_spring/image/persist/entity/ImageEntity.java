package com.example.image_use_spring.image.persist.entity;

import com.example.image_use_spring.common.entity.BaseTimeEntity;
import com.example.image_use_spring.member.persist.entity.MemberEntity;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageEntity extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String uuid;

  private String imagePath;

  private String imageFileName;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "memberId", nullable = false)
  private MemberEntity member;

  // 압축된 이미지와 썸네일이 원본 이미지를 참조하는 필드
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "originalImageId")
  private ImageEntity originalImage;

}
