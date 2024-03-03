package com.example.image_use_spring.member.security.oauth2;

import com.example.image_use_spring.member.dto.constants.AuthType;
import com.example.image_use_spring.member.persist.entity.MemberEntity;
import com.example.image_use_spring.member.persist.repository.MemberRepository;
import com.example.image_use_spring.member.security.TokenProvider;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

  private final TokenProvider tokenProvider;
  private final MemberRepository memberRepository;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {
    CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
    String email = (String) customOAuth2User.getAttributes().get("email");
    MemberEntity member = memberRepository.findByEmail(email)
        .orElseGet(() -> memberRepository.save(customOAuth2User.toEntity()));

    if (member.getAuthType() == AuthType.EMAIL) {
      response.setContentType("application/json");
      response.setCharacterEncoding("UTF-8");
      response.getWriter().write("{\"message\": \"이메일 가입자입니다.\"}");
      response.setStatus(HttpServletResponse.SC_OK);
      //TODO: 클라이언트 쪽에서 처음 페이지 받기
      response.sendRedirect("http://localhost:8080/");
      return;
    }

    String accessToken = tokenProvider.generateAccessToken(customOAuth2User);
    String refreshToken = tokenProvider.generateRefreshToken();
    tokenProvider.sendAccessAndRefreshToken(response, accessToken, refreshToken);
    response.sendRedirect("http://localhost:8080/");
    
  }
}
