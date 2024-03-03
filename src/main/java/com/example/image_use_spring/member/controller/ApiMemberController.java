package com.example.image_use_spring.member.controller;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class ApiMemberController {

  private final MemberServiceImpl memberService;

//  @PostMapping("/sign-out")
//  public ResponseEntity<?> signOut() {
//    ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", "")
//        .httpOnly(true)
//        .path("/")
//        .sameSite("None")
//        .secure(true)
//        .maxAge(0)
//        .build();
//
//    ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", "")
//        .httpOnly(true)
//        .path("/")
//        .sameSite("None")
//        .secure(true)
//        .maxAge(0)
//        .build();
//
//    HttpHeaders headers = new HttpHeaders();
//    headers.add(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
//    headers.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
//
//    return ResponseEntity.status(HttpStatus.OK).headers(headers).body("로그아웃 되었습니다.");
//  }
}
