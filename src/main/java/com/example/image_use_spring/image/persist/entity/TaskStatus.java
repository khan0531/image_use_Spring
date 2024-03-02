package com.example.image_use_spring.image.persist.entity;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskStatus {
  @Id
  private String uuid;
  private String status;
}