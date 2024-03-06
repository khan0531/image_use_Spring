package com.example.image_use_spring.member.dto;

import com.example.image_use_spring.member.dto.constants.AuthType;
import com.example.image_use_spring.member.persist.entity.MemberEntity;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


public class EmailSignUpDto {

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Request {
    @NotBlank(message = "이름을 입력해주세요.")
    private String name;

    @Email(message = "이메일 형식이 아닙니다.")
    private String email;

    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
    private String password;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static  class Response {
    private Long id;

    private AuthType authType;

    private String name;

    private String email;

    public static Response fromEntity(MemberEntity memberEntity) {
      return Response.builder()
          .id(memberEntity.getId())
          .authType(memberEntity.getAuthType())
          .name(memberEntity.getName())
          .email(memberEntity.getEmail())
          .build();
    }
  }
}
