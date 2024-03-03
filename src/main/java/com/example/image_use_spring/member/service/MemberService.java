package com.example.image_use_spring.member.service;

import com.example.image_use_spring.member.dto.EmailCodeVerifyRequestDto;
import com.example.image_use_spring.member.dto.EmailSignInRequestDto;
import com.example.image_use_spring.member.dto.EmailSignUpDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface MemberService extends UserDetailsService {
  EmailSignUpDto.Response signUpWithEmail(EmailSignUpDto.Request memberSignUpRequest);

  boolean verifyEmail(EmailCodeVerifyRequestDto requestDto);

  void signInWithEmail(EmailSignInRequestDto emailSignInRequestDto);


}
