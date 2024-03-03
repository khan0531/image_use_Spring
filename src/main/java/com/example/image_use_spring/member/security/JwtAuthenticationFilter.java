package com.example.image_use_spring.member.security;


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

    Optional<String> accessTokenOpt = this.tokenProvider.extractAccessToken(request)
        .filter(tokenProvider::validateToken);

    if (accessTokenOpt.isPresent()) {
      String accessToken = accessTokenOpt.get();
      Authentication auth = getAuthentication(accessToken);
      SecurityContextHolder.getContext().setAuthentication(auth);
      filterChain.doFilter(request, response);
    } else {
      String refreshToken = this.tokenProvider.extractRefreshToken(request)
          .filter(tokenProvider::validateToken)
          .orElse(null);

      if (refreshToken != null) {
        refreshToken = checkRefreshTokenAndReIssueAccessToken(response, refreshToken);
        Optional<MemberEntity> memberEntityOpt = memberRepository.findByRefreshToken(refreshToken);
        if (memberEntityOpt.isPresent()) {
          Member member = Member.fromEntity(memberEntityOpt.get());
          String newAccessToken = this.tokenProvider.generateAccessToken(member);
          Authentication newAuth = getAuthentication(newAccessToken);
          SecurityContextHolder.getContext().setAuthentication(newAuth);
        }

        filterChain.doFilter(request, response);
      } else {
        filterChain.doFilter(request, response);
      }
    }
  }

  public Authentication getAuthentication(String jwt) {
    UserDetails userDetails = this.memberService.loadUserByUsername(this.tokenProvider.getUsername(jwt));

    return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
  }

  public String checkRefreshTokenAndReIssueAccessToken(HttpServletResponse response, String refreshToken) {
    AtomicReference<String> newRefreshTokenRef = new AtomicReference<>();

    memberRepository.findByRefreshToken(refreshToken)
        .ifPresent(memberEntity -> {
          Member member = Member.fromEntity(memberEntity);
          String reIssuedRefreshToken = reIssueRefreshToken(member);
          tokenProvider.sendAccessAndRefreshToken(response, tokenProvider.generateAccessToken(member),
              reIssuedRefreshToken);
          newRefreshTokenRef.set(reIssuedRefreshToken);
        });

    return newRefreshTokenRef.get();
  }

  private String reIssueRefreshToken(Member member) {
    String reIssuedRefreshToken = this.tokenProvider.generateRefreshToken();
    member.updateRefreshToken(reIssuedRefreshToken);
    this.memberRepository.saveAndFlush(member.toEntity());
    return reIssuedRefreshToken;
  }
}