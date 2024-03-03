package com.example.image_use_spring.member.controller;

import com.example.image_use_spring.member.dto.EmailCodeRequestDto;
import com.example.image_use_spring.member.dto.EmailCodeVerifyRequestDto;
import com.example.image_use_spring.member.dto.EmailSignInRequestDto;
import com.example.image_use_spring.member.dto.EmailSignUpDto;
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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
class MemberController {

  private final MemberService memberService;
  @RequestMapping("/member/sign-in/email")
  public String signInWithEmail() {

    return "member/sign-in/email";
  }

  @PostMapping("/member/sign-in/email")
  public String signInWithEmail(@Valid EmailSignInRequestDto emailSignInRequestDto) {
    memberService.signInWithEmail(emailSignInRequestDto);
    return "index";
  }

  @GetMapping("/member/sign-up/email")
  public String signUpWithEmail() {
    return "member/sign-up/email";
  }

  @PostMapping("/member/sign-up/email")
  public String signUpWithEmailSubmit(Model model, HttpServletRequest request
      , EmailSignUpDto.Request memberSignUpRequest) {
    EmailSignUpDto.Response response = memberService.signUpWithEmail(memberSignUpRequest);

    model.addAttribute("result", true);

    return "/member/sign-up/email_complete";
  }

  @RequestMapping("/member/sign-out")
  public String signOut() {
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

    return "index";
  }

  @GetMapping("/member/find/password")
  public String findPassword() {
    return "member/find_password";
  }

  @PostMapping("/member/find/password")
  public String findPasswordSubmit(
      Model model,
      ResetPasswordLinkRequestDto request) {
    boolean result = memberService.sendResetPasswordLink(request.getEmail());

    model.addAttribute("result", result);

    return "member/find_password_result";
  }

  @PostMapping("/member/reset-password/{memberId}/t/{token}")
  public String resetPassword(
      @PathVariable Long memberId,
      @PathVariable String token,
      @RequestBody ResetPasswordRequestDto requestDto) {

    boolean response = memberService.resetPassword(memberId, token, requestDto.getPassword());
    return "member/reset_password_result";
  }

//  @PostMapping("/member/sign-up/email-verifications/{token}")
//  public String verifyEmail(EmailCodeVerifyRequestDto requestDto, @PathVariable String token) {
//    boolean response = memberService.verifyEmail(token);
//    return "member/verify_email";
//  }
}
