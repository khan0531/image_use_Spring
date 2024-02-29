package com.example.image_use_awss3.exception.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

  NOT_VALID_FILE_TYPE("지원하지 않는 파일 형식입니다."),
  FILE_NOT_FOUND("파일을 찾을 수 없습니다."),
  INTERNAL_SERVER_ERROR("서버 오류가 발생 했습니다.");

  private final String description;
}
