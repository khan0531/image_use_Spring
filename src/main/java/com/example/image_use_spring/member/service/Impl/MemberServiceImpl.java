package com.example.image_use_spring.member.service.Impl;

import static com.example.image_use_spring.exception.type.ErrorCode.MEMBER_ALREADY_EXIST;
import static com.example.image_use_spring.exception.type.ErrorCode.MEMBER_NOT_AUTHORIZED;
import static com.example.image_use_spring.exception.type.ErrorCode.MEMBER_NOT_FOUND;
import static com.example.image_use_spring.exception.type.ErrorCode.MEMBER_PASSWORD_NOT_MATCH;
import static com.example.image_use_spring.exception.type.ErrorCode.MEMBER_VERIFICATION_NOT_ACTIVE;
import static com.example.image_use_spring.exception.type.ErrorCode.MEMBER_VERIFICATION_NOT_REQUEST;

import com.example.image_use_spring.common.AwsSimpleEmailService;
import com.example.image_use_spring.exception.MemberException;
import com.example.image_use_spring.exception.type.ErrorCode;
import com.example.image_use_spring.member.domain.Member;
import com.example.image_use_spring.member.dto.EmailCodeVerifyRequestDto;
import com.example.image_use_spring.member.dto.EmailSignInRequestDto;
import com.example.image_use_spring.member.dto.EmailSignUpDto;
import com.example.image_use_spring.member.persist.entity.MemberEntity;
import com.example.image_use_spring.member.persist.entity.VerificationCode;
import com.example.image_use_spring.member.persist.repository.MemberRepository;
//import com.example.image_use_spring.member.persist.repository.VerificationCodeRepository;
import com.example.image_use_spring.member.security.TokenProvider;
import com.example.image_use_spring.member.service.MemberService;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;
  private final TokenProvider tokenProvider;

//  private final VerificationCodeRepository verificationCodeRepository;

  private final AwsSimpleEmailService awsSimpleEmailService;

  @Value("${spring.profiles.active}")
  private String activeProfile;
  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    return memberRepository.findByEmail(email)
        .map(Member::fromEntity)
        .map(member -> {
          if (member.getPassword() == null) {
            member.setPassword("123456789");
          }
          return member;
        })
        .orElseThrow(() -> new UsernameNotFoundException("가입된 이메일이 아닙니다. -> " + email));
  }

  @Override
  public EmailSignUpDto.Response signUpWithEmail(EmailSignUpDto.Request memberSignUpRequest) {

    memberRepository.findByEmail(memberSignUpRequest.getEmail()).ifPresent(member -> {
      throw new MemberException(MEMBER_ALREADY_EXIST);
    });

//    if ("prod".equals(activeProfile)) {
//      VerificationCode verificationCode = verificationCodeRepository.findById(memberSignUpRequest.getEmail())
//          .orElseThrow(() -> new MemberException(MEMBER_VERIFICATION_NOT_REQUEST));
//
//      if (!verificationCode.isVerified()) {
//        throw new MemberException(MEMBER_VERIFICATION_NOT_ACTIVE);
//      }
//    }

    Member member = Member.fromSignUpDto(memberSignUpRequest);
    member.passwordEncode(passwordEncoder);
    MemberEntity memberEntity = memberRepository.save(member.toEntity());

    return EmailSignUpDto.Response.fromEntity(memberEntity);
  }

  public void signInWithEmail(EmailSignInRequestDto emailSignInRequestDto) {
    Member member = (Member) loadUserByUsername(emailSignInRequestDto.getEmail());
    if (!passwordEncoder.matches(emailSignInRequestDto.getPassword(), member.getPassword())) {
      throw new MemberException(MEMBER_PASSWORD_NOT_MATCH);
    }

    HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder
        .currentRequestAttributes()).getResponse();

    String accessToken = tokenProvider.generateAccessToken(member);
    String refreshToken = tokenProvider.generateRefreshToken();
    member.setRefreshToken(refreshToken);
    memberRepository.save(member.toEntity());

    tokenProvider.sendAccessAndRefreshToken(response, accessToken, refreshToken);
  }

  public String sendEmailVerificationCode(String email) {
//    String code = verificationCodeGenerator();
//
//    awsSimpleEmailService.sendEmail(email, "이메일 가입 인증 코드입니다.", code);
//
//    VerificationCode verificationCode =
//        new VerificationCode()
//        .builder()
//        .email(email)
//        .code(code)
//        .isVerified(false)
//        .build();
//    verificationCodeRepository.save(verificationCode);

    return "이메일 인증 메일을 전송했습니다.";
  }

  @Override
  public boolean verifyEmail(EmailCodeVerifyRequestDto requestDto) {
//    VerificationCode verificationCode = verificationCodeRepository.findById(requestDto.getEmail())
//        .orElseThrow(() -> new IllegalArgumentException("인증 코드가 존재하지 않습니다."));
//
//    if (verificationCode != null && verificationCode.getCode().equals(requestDto.getCode())) {
//      verificationCode.setVerified(true);
//      verificationCode.setTtl(TimeUnit.MINUTES.toSeconds(20));
//      verificationCodeRepository.save(verificationCode); // 인증 상태 업데이트
//
//      return true;
//    }

    return false;
  }

  public MemberEntity validateAndGetMember(Long memberId, Member member) {
    if (!Objects.equals(memberId, member.getId())) {
      throw new MemberException(MEMBER_NOT_AUTHORIZED);
    }

    return memberRepository.findById(memberId).orElseThrow(
        ()->new MemberException(MEMBER_NOT_FOUND));
  }

  public MemberEntity validateAndGetMember(Member member) {
    return memberRepository.findById(member.getId()).orElseThrow(
        ()->new MemberException(MEMBER_NOT_FOUND));
  }

  private String verificationCodeGenerator() {
    Random random = new Random();
    int verificationCode = 100000 + random.nextInt(900000);
    return String.valueOf(verificationCode);
  }
}
