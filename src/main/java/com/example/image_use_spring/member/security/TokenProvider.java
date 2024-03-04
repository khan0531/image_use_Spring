package com.example.image_use_spring.member.security;


import com.example.image_use_spring.member.persist.repository.MemberRepository;
import com.example.image_use_spring.member.security.oauth2.CustomOAuth2User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenProvider {

  public static final String ACCESS_TOKEN_HEADER = "Authorization";
  public static final String TOKEN_PREFIX = "Bearer ";
  private static final String REFRESH_TOKEN_HEADER = "Authorization-refresh";
  
  private final MemberRepository memberRepository;

  @Value("${jwt.secretKey}")
  private String secretKey;

  @Value("${jwt.access.expiration}") // 1시간
  private Long accessTokenExpirationPeriod;

  @Value("${jwt.refresh.expiration}") // 7일
  private Long refreshTokenExpirationPeriod;

  private JwtParser jwtParser;

  private synchronized JwtParser getJwtParser() {
    if (this.jwtParser == null) {
      this.jwtParser = Jwts.parser().setSigningKey(this.secretKey);
    }
    return this.jwtParser;
  }

  private String generateAccessToken(String email) {
    Claims claims = Jwts.claims().setSubject(email);

    Date now = new Date();
    Date expireDate = new Date(now.getTime() + accessTokenExpirationPeriod);

    return Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(now)
        .setExpiration(expireDate)
        .signWith(SignatureAlgorithm.HS512, this.secretKey) // 사용할 암호화 알고리즘, 비밀키
        .compact();
  }

  public String generateAccessToken(UserDetails userDetails) {
    return this.generateAccessToken(userDetails.getUsername());
  }

  public String generateAccessToken(CustomOAuth2User customOAuth2User) {
    return this.generateAccessToken((String) customOAuth2User.getAttributes().get("email"));
  }

  public String generateRefreshToken() {
    Date now = new Date();
    Date expireDate = new Date(now.getTime() + refreshTokenExpirationPeriod);
    // jwt 발급
    return Jwts.builder()
        .setExpiration(expireDate)
        .signWith(SignatureAlgorithm.HS512, this.secretKey)
        .compact();
  }

  public String getUsername(String token) {
    return getJwtParser().parseClaimsJws(token).getBody().getSubject();
  }

  public boolean validateToken(String token) {
    try {
      Jws<Claims> claims = getJwtParser().parseClaimsJws(token);
      log.debug("claims: {}", claims);

      return !claims.getBody().getExpiration().before(new Date());
    } catch (Exception e) {
      return false;
    }
  }

  public void sendAccessAndRefreshToken(HttpServletResponse response, String accessToken, String refreshToken) {
    ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", accessToken)
        .httpOnly(true)
        .path("/")
        .sameSite("None")
        .secure(true)
        .build();

    ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
        .httpOnly(true)
        .path("/")
        .sameSite("None")
        .secure(true)
        .build();

    response.addHeader("Set-Cookie", accessTokenCookie.toString());
    response.addHeader("Set-Cookie", refreshTokenCookie.toString());

    log.info("Access Token, Refresh Token 쿠키 설정 완료");
  }

  public Optional<String> extractToken(HttpServletRequest request, String cookieName) {
    if (request.getCookies() == null) {
      return Optional.empty();
    }
    return Arrays.stream(request.getCookies())
        .filter(cookie -> cookie.getName().equals(cookieName))
        .findFirst()
        .map(Cookie::getValue);
  }

  public Optional<String> extractRefreshToken(HttpServletRequest request) {
    return extractToken(request, "refreshToken");
  }

  public Optional<String> extractAccessToken(HttpServletRequest request) {
    return extractToken(request, "accessToken");
  }
}
