package com.example.image_use_spring.member.security;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

public class UserAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

  @Override
  public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException exception) throws IOException, ServletException {

    String failureMessage = "로그인에 실패";

    if (exception instanceof InternalAuthenticationServiceException) {
      failureMessage = exception.getMessage();
    }

    setUseForward(true);
    setDefaultFailureUrl("/member/login?error=true");
    request.setAttribute("errorMessage", failureMessage);

    System.out.println("로그인 실패");

    super.onAuthenticationFailure(request, response, exception);
  }
}
