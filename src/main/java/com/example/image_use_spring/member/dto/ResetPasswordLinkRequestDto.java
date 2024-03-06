package com.example.image_use_spring.member.dto;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordLinkRequestDto {
  @NotBlank(message = "이메일 주소가 필요합니다.")
  private String email;
  private String name;
}
