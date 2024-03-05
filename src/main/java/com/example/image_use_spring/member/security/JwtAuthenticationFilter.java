package com.example.image_use_spring.member.security;


import static com.example.image_use_spring.exception.type.ErrorCode.MEMBER_RFTOKEN_NOT_FOUND;

import com.example.image_use_spring.exception.MemberException;
import com.example.image_use_spring.exception.type.ErrorCode;
import com.example.image_use_spring.member.domain.Member;
import com.example.image_use_spring.member.persist.entity.MemberEntity;
import com.example.image_use_spring.member.persist.repository.MemberRepository;
import com.example.image_use_spring.member.service.Impl.MemberServiceImpl;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final TokenProvider tokenProvider;
  private final MemberRepository memberRepository;
  private final MemberServiceImpl memberService;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    try {
      Optional<String> accessTokenOpt = tokenProvider.extractAccessToken(request)
          .filter(tokenProvider::validateToken);

      if (accessTokenOpt.isPresent()) {
        processAuthentication(accessTokenOpt.get(), request, response, filterChain);
      } else {
        String refreshToken = tokenProvider.extractRefreshToken(request)
            .filter(tokenProvider::validateToken)
            .orElse(null);

        if (refreshToken != null) {
          checkRefreshTokenAndReIssueAccessToken(response, refreshToken);
          filterChain.doFilter(request, response);
        } else {
          filterChain.doFilter(request, response);
        }
      }
    } catch (Exception e) {
      log.error("Security exception for user {} - {}", e.getMessage(), request.getRequestURI(), e);
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
  }

  private void processAuthentication(String accessToken, HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
    Authentication auth = getAuthentication(accessToken);
    SecurityContextHolder.getContext().setAuthentication(auth);
    filterChain.doFilter(request, response);
  }

  private Authentication getAuthentication(String jwt) {
    UserDetails userDetails = memberService.loadUserByUsername(tokenProvider.getUsername(jwt));
    log.debug("Setting security context for user '{}'", userDetails.getUsername());
    return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
  }

  private void reIssueRefreshToken(Member member) {
    String reIssuedRefreshToken = tokenProvider.generateRefreshToken();
    member.updateRefreshToken(reIssuedRefreshToken);
    memberRepository.save(member.toEntity());
  }

  private String checkRefreshTokenAndReIssueAccessToken(HttpServletResponse response, String refreshToken) {
    return memberRepository.findByRefreshToken(refreshToken)
        .map(memberEntity -> {
          Member member = Member.fromEntity(memberEntity);
          reIssueRefreshToken(member);
          String newAccessToken = tokenProvider.generateAccessToken(member);

          Authentication newAuth = getAuthentication(newAccessToken);
          SecurityContextHolder.getContext().setAuthentication(newAuth);

          tokenProvider.sendAccessAndRefreshToken(response, newAccessToken, member.getRefreshToken());
          return member.getRefreshToken();
        }).orElseThrow(() -> new MemberException(MEMBER_RFTOKEN_NOT_FOUND));
  }
}