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

    Optional<String> accessTokenOpt = tokenProvider.extractAccessToken(request)
        .filter(tokenProvider::validateToken);

    if (accessTokenOpt.isPresent()) {
      String accessToken = accessTokenOpt.get();
      Authentication auth = getAuthentication(accessToken);
      SecurityContextHolder.getContext().setAuthentication(auth);
      filterChain.doFilter(request, response);
    } else {
      String refreshToken = tokenProvider.extractRefreshToken(request)
          .filter(tokenProvider::validateToken)
          .orElse(null);

      if (refreshToken != null) {
        Optional<MemberEntity> optionalMemberEntity = memberRepository.findByRefreshToken(refreshToken);
        if (optionalMemberEntity.isPresent()) {
          Member member = Member.fromEntity(optionalMemberEntity.get());
          reIssueRefreshToken(member);
          String newAccessToken = tokenProvider.generateAccessToken(member);
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
    UserDetails userDetails = memberService.loadUserByUsername(tokenProvider.getUsername(jwt));

    return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
  }

  private String reIssueRefreshToken(Member member) {
    String reIssuedRefreshToken = tokenProvider.generateRefreshToken();
    member.updateRefreshToken(reIssuedRefreshToken);
    memberRepository.saveAndFlush(member.toEntity());
    return reIssuedRefreshToken;
  }

//  public String checkRefreshTokenAndReIssueAccessToken(HttpServletResponse response, String refreshToken) {
//    AtomicReference<String> newRefreshTokenRef = new AtomicReference<>();
//
//    memberRepository.findByRefreshToken(refreshToken)
//        .ifPresent(memberEntity -> {
//          Member member = Member.fromEntity(memberEntity);
//          String reIssuedRefreshToken = reIssueRefreshToken(member);
//          tokenProvider.sendAccessAndRefreshToken(response, tokenProvider.generateAccessToken(member),
//              reIssuedRefreshToken);
//          newRefreshTokenRef.set(reIssuedRefreshToken);
//        });
//
//    return newRefreshTokenRef.get();
//  }
}