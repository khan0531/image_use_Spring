package com.example.image_use_spring.member.service;

import com.example.image_use_spring.member.domain.Member;
import com.example.image_use_spring.member.dto.EmailCodeVerifyRequestDto;
import com.example.image_use_spring.member.dto.EmailSignInRequestDto;
import com.example.image_use_spring.member.dto.EmailSignUpDto;
import com.example.image_use_spring.member.dto.ResetPasswordLinkRequestDto;
import com.example.image_use_spring.member.persist.entity.MemberEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface MemberService extends UserDetailsService {
  EmailSignUpDto.Response signUpWithEmail(EmailSignUpDto.Request memberSignUpRequest);

  boolean verifyEmail(EmailCodeVerifyRequestDto requestDto);

  void signInWithEmail(EmailSignInRequestDto emailSignInRequestDto);

  String sendEmailVerificationCode(String email);


  MemberEntity validateAndGetMember(Member member);

  MemberEntity validateAndGetMember(Long memberId, Member member);
}
