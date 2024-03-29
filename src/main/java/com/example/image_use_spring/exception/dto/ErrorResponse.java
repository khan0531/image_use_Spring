package com.example.image_use_spring.exception.dto;

import com.example.image_use_spring.exception.type.ErrorCode;
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
public class ErrorResponse {
  private ErrorCode errorCode;
  private String errorMessage;
}
