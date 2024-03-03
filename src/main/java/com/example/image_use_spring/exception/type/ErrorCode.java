package com.example.image_use_spring.exception.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
  /**
   * Member
   */
  MEMBER_NOT_FOUND("해당하는 Member를 찾을 수 없습니다."),
  MEMBER_ALREADY_EXIST("이미 존재하는 Member입니다."),
  MEMBER_NOT_VALID("유효하지 않은 Member입니다."),
  MEMBER_NOT_MATCH("Member 정보가 일치하지 않습니다."),
  MEMBER_NOT_AUTHORIZED("Member 권한이 없습니다."),
  MEMBER_VERIFICATION_NOT_REQUEST("Member 인증이 요청되지 않았습니다."),
  MEMBER_VERIFICATION_ALREADY_ACTIVE("이미 Member 인증이 되어있습니다."),
  MEMBER_VERIFICATION_NOT_ACTIVE("Member 인증이 되지 않았습니다."),
  MEMBER_NOT_EXIST("존재하지 않는 Member입니다."),

  /**
   * Image
   */
  NOT_FOUND_TASK("해당하는 Task를 찾을 수 없습니다."),
  NOT_VALID_FILE_TYPE("지원하지 않는 파일 형식입니다."),
  FILE_NOT_FOUND("파일을 찾을 수 없습니다."),
  INTERNAL_SERVER_ERROR("서버 오류가 발생 했습니다.");

  private final String description;
}
