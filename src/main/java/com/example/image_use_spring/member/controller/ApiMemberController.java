package com.example.image_use_spring.member.controller;

import static com.example.image_use_spring.exception.type.ErrorCode.MEMBER_VERIFICATION_CODE_NOT_VALID;

import com.example.image_use_spring.exception.MemberException;
import com.example.image_use_spring.member.dto.EmailCodeRequestDto;
import com.example.image_use_spring.member.dto.EmailCodeVerifyRequestDto;
import com.example.image_use_spring.member.dto.EmailSignInRequestDto;
import com.example.image_use_spring.member.dto.EmailSignUpDto;
import com.example.image_use_spring.member.dto.ResetPasswordLinkRequestDto;
import com.example.image_use_spring.member.service.Impl.MemberServiceImpl;
import com.example.image_use_spring.member.service.MemberService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequiredArgsConstructor
public class ApiMemberController {

//  private final MemberService memberService;
//  @RequestMapping("/member/sign-in/email")
//  public String signInWithEmail() {
//
//    return "member/sign-in/email";
//  }
//
//  @PostMapping("/member/sign-in/email")
//  public String signInWithEmail(@Valid EmailSignInRequestDto emailSignInRequestDto) {
//    memberService.signInWithEmail(emailSignInRequestDto);
//    return "index";
//  }

}
