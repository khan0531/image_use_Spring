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
  MEMBER_VERIFICATION_CODE_NOT_VALID("유효하지 않은 Member 인증 코드입니다."),
  MEMBER_NOT_EXIST("존재하지 않는 Member입니다."),
  MEMBER_PASSWORD_NOT_MATCH("비밀번호가 일치하지 않습니다."),
  MEMBER_RFTOKEN_NOT_FOUND("Refresh Token을 찾을 수 없습니다."),

  /**
   * Image
   */
  NOT_FOUND_TASK("해당하는 Task를 찾을 수 없습니다."),
  NOT_VALID_FILE_TYPE("지원하지 않는 파일 형식입니다."),
  FILE_NOT_FOUND("파일을 찾을 수 없습니다."),
  INTERNAL_SERVER_ERROR("서버 오류가 발생 했습니다."),
  OCR_FAILED("OCR 처리에 실패했습니다."),
  OCR_NOT_FOUND("해당하는 OCR 결과를 찾을 수 없습니다."),

  /**
   * Group
   */
  GROUP_NOT_FOUND("해당하는 Group을 찾을 수 없습니다."),
  GROUP_IS_DELETED("삭제된 Group입니다."),
  GROUP_ALREADY_EXIST("이미 존재하는 Group입니다."),
  GROUP_NOT_VALID("유효하지 않은 Group입니다."),
  GROUP_NOT_MATCH("Group 정보가 일치하지 않습니다."),
  GROUP_NOT_AUTHORIZED("Group 권한이 없습니다."),
  GROUP_NOT_EXIST("존재하지 않는 Group입니다."),
  ALREADY_GROUP_MEMBER("이미 Group에 속해있습니다."),
  GROUP_NOT_MEMBER("해당하는 Member가 Group에 속해있지 않습니다."),
  GROUP_NOT_ADMIN("해당하는 Member가 Group의 admin이 아닙니다. 수정 권한이 없습니다."),
  GROUP_NOT_MEMBER_VERIFICATION("해당하는 Member가 Group의 Member가 아닙니다."),
  GROUP_FULL("Group의 정원이 꽉 찼습니다."),

  /**
   * Email
   */
  EMAIL_SEND_FAILED("이메일 전송에 실패했습니다.");

  /**
   * Message
   */


  private final String description;
}
