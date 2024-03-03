package com.example.image_use_spring.member.controller;

import com.example.image_use_spring.member.dto.EmailCodeRequestDto;
import com.example.image_use_spring.member.dto.EmailSignUpDto;
import com.example.image_use_spring.member.service.MemberService;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
class MemberController {

  private final MemberService memberService;
  @RequestMapping("/member/login")
  public String login() {

    return "member/login";
  }

  @GetMapping("/member/sign-up/email")
  public String signUpWithEmail() {
    return "member/sign-up/email";
  }

  @PostMapping("/member/sign-up/email")
  public String signUpWithEmailSubmit(Model model, HttpServletRequest request
      , EmailSignUpDto.Request memberSignUpRequest) {
    EmailSignUpDto.Response response = memberService.signUpWithEmail(memberSignUpRequest);

    model.addAttribute("result", response);

    return "member/sign-up/email-complete";
  }

  @PostMapping("/member/sign-up/email-verifications")
  public ResponseEntity<?> sendEmailVerificationCode(@RequestBody EmailCodeRequestDto requestDto) {
   memberService.sendEmailVerificationCode(requestDto.getEmail());
    return ResponseEntity.ok().body("인증 코드가 이메일로 전송되었습니다.");
  }


}
