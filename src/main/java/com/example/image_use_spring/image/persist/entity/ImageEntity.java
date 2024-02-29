package com.example.image_use_awss3.image.persist.entity;

import javax.persistence.*;
import lombok.Data;

@Entity
@Data
public class ImageEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String imagePath;

  private String imageFileName;

}
