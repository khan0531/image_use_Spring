package com.example.image_use_spring.member.controller;

import static com.example.image_use_spring.exception.type.ErrorCode.MEMBER_VERIFICATION_CODE_NOT_VALID;

import com.example.image_use_spring.exception.MemberException;
import com.example.image_use_spring.member.dto.EmailCodeVerifyRequestDto;
import com.example.image_use_spring.member.dto.EmailSignInRequestDto;
import com.example.image_use_spring.member.dto.EmailSignUpDto;
import com.example.image_use_spring.member.service.Impl.MemberServiceImpl;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class ApiMemberController {

  private final MemberServiceImpl memberService;


  public ResponseEntity<?> verifyEmail(@RequestBody EmailCodeVerifyRequestDto requestDto) {
    MemberException memberException = new MemberException(MEMBER_VERIFICATION_CODE_NOT_VALID);
    try {
      boolean isVerified = memberService.verifyEmail(requestDto);

      if (isVerified) {
        return ResponseEntity.ok().body("이메일 인증에 성공했습니다.");
      } else {
        return ResponseEntity.badRequest().body("올바른 코드가 아닙니다.");
      }
    } catch (MemberException e) {
      return ResponseEntity.badRequest().body(e.getErrorMessage());
    }
  }
}
