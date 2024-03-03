package com.example.image_use_spring.member.service.Impl;

import static com.example.image_use_spring.exception.type.ErrorCode.MEMBER_ALREADY_EXIST;
import static com.example.image_use_spring.exception.type.ErrorCode.MEMBER_NOT_FOUND;
import static com.example.image_use_spring.exception.type.ErrorCode.MEMBER_NOT_MATCH;
import static com.example.image_use_spring.exception.type.ErrorCode.MEMBER_VERIFICATION_NOT_ACTIVE;
import static com.example.image_use_spring.exception.type.ErrorCode.MEMBER_VERIFICATION_NOT_REQUEST;

import com.example.image_use_spring.common.SimpleEmailService;
import com.example.image_use_spring.exception.MemberException;
import com.example.image_use_spring.exception.type.ErrorCode;
import com.example.image_use_spring.image.persist.repository.ImageRepository;
import com.example.image_use_spring.member.domain.Member;
import com.example.image_use_spring.member.dto.EmailCodeVerifyRequestDto;
import com.example.image_use_spring.member.dto.EmailSignInRequestDto;
import com.example.image_use_spring.member.dto.EmailSignUpDto;
import com.example.image_use_spring.member.persist.entity.MemberEntity;
import com.example.image_use_spring.member.persist.entity.PasswordReset;
import com.example.image_use_spring.member.persist.entity.VerificationCode;
import com.example.image_use_spring.member.persist.repository.MemberRepository;
import com.example.image_use_spring.member.persist.repository.PasswordResetRepository;
import com.example.image_use_spring.member.persist.repository.VerificationCodeRepository;
import com.example.image_use_spring.member.security.TokenProvider;
import com.example.image_use_spring.member.service.MemberService;
import com.example.image_use_spring.member.util.MemberUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
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

  private final ImageRepository imageRepository;
  private final VerificationCodeRepository verificationCodeRepository;
  private final PasswordResetRepository passwordResetRepository;

  private final SimpleEmailService simpleEmailService;
  private final MemberUtil memberUtil;

  @Value("${spring.profiles.active}")
  private String activeProfile;

  private final static String[] categoryNames = {
      "가전/가구", "가전생활/서비스", "교육/학원", "미용",
      "스포츠/문화/레저", "여행/교통", "요식/유흥", "유통",
      "음/식료품", "의료", "의류/잡화", "자동차",
      "전자상거래", "주유"
  };

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

  public EmailSignUpDto.Response signUpWithEmail(EmailSignUpDto.Request memberSignUpRequest) {

    this.memberRepository.findByEmail(memberSignUpRequest.getEmail()).ifPresent(member -> {
      throw new MemberException(MEMBER_ALREADY_EXIST);
    });

    if ("prod".equals(activeProfile)) {
      VerificationCode verificationCode = verificationCodeRepository.findById(memberSignUpRequest.getEmail())
          .orElseThrow(() -> new MemberException(MEMBER_VERIFICATION_NOT_REQUEST));

      if (!verificationCode.isVerified()) {
        throw new MemberException(MEMBER_VERIFICATION_NOT_ACTIVE);
      }
    }

    Member member = Member.fromSignUpDto(memberSignUpRequest);
    member.passwordEncode(passwordEncoder);
    MemberEntity memberEntity = memberRepository.save(member.toEntity());

    return EmailSignUpDto.Response.fromEntity(memberEntity);
  }

  @Override
  public boolean verifyEmail(EmailCodeVerifyRequestDto requestDto) {
    VerificationCode verificationCode = verificationCodeRepository.findById(requestDto.getEmail())
        .orElseThrow(() -> new IllegalArgumentException("인증 코드가 존재하지 않습니다."));

    if (verificationCode != null && verificationCode.getCode().equals(requestDto.getCode())) {
      verificationCode.setVerified(true);
      verificationCode.setTtl(TimeUnit.MINUTES.toSeconds(20));
      verificationCodeRepository.save(verificationCode); // 인증 상태 업데이트

      return true;
    }

    return false;
  }

  public void signInWithEmail(EmailSignInRequestDto emailSignInRequestDto) {
    Member member = (Member) loadUserByUsername(emailSignInRequestDto.getEmail());
    if (!passwordEncoder.matches(emailSignInRequestDto.getPassword(), member.getPassword())) {
      throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
    }

    HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder
        .currentRequestAttributes()).getResponse();

    String accessToken = tokenProvider.generateAccessToken(member);
    String refreshToken = tokenProvider.generateRefreshToken();

    tokenProvider.sendAccessAndRefreshToken(response, accessToken, refreshToken);
  }

  public String sendEmailVerificationCode(String email) {
    String code = memberUtil.verificationCodeGenerator();

    simpleEmailService.sendEmail(email, "가게그만가계 가입 인증 코드입니다.",
        String.format("%s/member/reset-password/%d/t/%s", "http://localhost:8080", code));

    VerificationCode verificationCode =
        new VerificationCode()
        .builder()
        .email(email)
        .code(code)
        .isVerified(false)
        .build();
    verificationCodeRepository.save(verificationCode);

    return "이메일 인증 메일을 전송했습니다.";
  }

  public boolean sendResetPasswordLink(String email) {
    String token = UUID.randomUUID().toString().replace("-", "");
    MemberEntity memberEntity = memberRepository.findMemberIdByEmail(email)
        .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));

    Long memberId = memberEntity.getId();

    simpleEmailService.sendEmail(email, "가게그만가계 비밀번호 재설정 링크입니다.",
        String.format("%s/member/reset-password/%d/t/%s", "http://localhost:8080", memberId, token));

    PasswordReset passwordReset = new PasswordReset().builder()
        .email(email)
        .memberId(memberId)
        .token(token)
        .build();
    passwordResetRepository.save(passwordReset);

    return true;
  }

  public boolean resetPassword(Long memberId, String token, String newPassword) {
    PasswordReset passwordReset = passwordResetRepository.findById(memberId)
        .orElseThrow(() -> new IllegalArgumentException("비밀번호 재설정 토큰이 존재하지 않습니다."));

    if (!passwordReset.getToken().equals(token)) {
      throw new IllegalArgumentException("비밀번호 재설정 토큰이 일치하지 않습니다.");
    }

    MemberEntity memberEntity = memberRepository.findById(passwordReset.getMemberId())
        .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

    memberEntity.setPassword(passwordEncoder.encode(newPassword));
    memberRepository.save(memberEntity);

    return true;
  }


}
