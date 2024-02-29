package com.example.image_use_spring.exception;


import static com.example.image_use_spring.exception.type.ErrorCode.INTERNAL_SERVER_ERROR;

import com.example.image_use_spring.exception.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ImageException.class)
  public ErrorResponse handleImageException(ImageException e) {
    log.error("handleImageException", e);
    return ErrorResponse.builder()
        .errorCode(e.getErrorCode())
        .errorMessage(e.getErrorMessage())
        .build();
  }

  @ExceptionHandler(Exception.class)
  public ErrorResponse handleException(Exception e) {
    log.error("Exception", e);
    return ErrorResponse.builder()
        .errorCode(INTERNAL_SERVER_ERROR)
        .errorMessage(INTERNAL_SERVER_ERROR.getDescription())
        .build();
  }
}
