package com.example.image_use_spring.member.controller;

import com.example.image_use_spring.member.domain.Member;
import com.example.image_use_spring.member.dto.EmailCodeRequestDto;
import com.example.image_use_spring.member.dto.EmailCodeVerifyRequestDto;
import com.example.image_use_spring.member.dto.EmailSignInRequestDto;
import com.example.image_use_spring.member.dto.EmailSignUpDto;
import com.example.image_use_spring.member.dto.PasswordChangeRequestDto;
import com.example.image_use_spring.member.dto.ResetPasswordLinkRequestDto;
import com.example.image_use_spring.member.dto.ResetPasswordRequestDto;
import com.example.image_use_spring.member.service.MemberService;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
class MemberController {

  private final MemberService memberService;

  @PostMapping("/sign-up/email")
  public ResponseEntity<?> signUpWithEmail(@RequestBody @Valid EmailSignUpDto.Request memberSignUpRequest) {
    EmailSignUpDto.Response response = memberService.signUpWithEmail(memberSignUpRequest);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/sign-in/email")
  public ResponseEntity<?> signInWithEmail(@RequestBody @Valid EmailSignInRequestDto emailSignInRequestDto) {
    memberService.signInWithEmail(emailSignInRequestDto);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/email-verifications")
  public ResponseEntity<?> sendEmailVerificationCode(@RequestBody EmailCodeRequestDto requestDto) {
    String response = memberService.sendEmailVerificationCode(requestDto.getEmail());
    return ResponseEntity.ok(response);
  }

  @PutMapping("/email-verifications")
  public ResponseEntity<?> verifyEmail(@RequestBody EmailCodeVerifyRequestDto requestDto) {
    try {
      boolean isVerified = memberService.verifyEmail(requestDto);

      if (isVerified) {
        return ResponseEntity.ok().body("이메일 인증에 성공했습니다.");
      } else {
        return ResponseEntity.badRequest().body("올바른 코드가 아닙니다.");
      }
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @PostMapping("/sign-out")
  public ResponseEntity<?> signOut() {
    ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", "")
        .httpOnly(true)
        .path("/")
        .sameSite("None")
        .secure(true)
        .maxAge(0)
        .build();

    ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", "")
        .httpOnly(true)
        .path("/")
        .sameSite("None")
        .secure(true)
        .maxAge(0)
        .build();

    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
    headers.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

    return ResponseEntity.status(HttpStatus.OK).headers(headers).body("로그아웃 되었습니다.");
  }

  //TODO: 비밀번호 변경, 비밀번호 초기화, 내 정보 업데이트, 내 정보 조회
}
