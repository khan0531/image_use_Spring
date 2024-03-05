package com.example.image_use_spring.member.security;


import com.example.image_use_spring.member.security.oauth2.OAuth2LoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

  private final JwtAuthenticationFilter authenticationFilter;
  private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
  private final OAuth2UserService oAuth2UserService;

  @Bean
  UserAuthenticationFailureHandler getFailureHandler() {
    return new UserAuthenticationFailureHandler();
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .httpBasic().disable()
        .csrf().disable()
        .headers().frameOptions().disable()
        .and()
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

        .and()
        .authorizeRequests()
        .antMatchers("/h2-console/**").permitAll()
        .antMatchers("/", "/member/sign-up/**", "/member/email-auth", "/member/find-password/**",
            "/member/reset/password", "/member/find/password").permitAll()
        .antMatchers("/member/sign-up/**", "/member/sign-in/**").permitAll()
        .antMatchers("/member/email-verifications/**", "/member/reset-password/**").permitAll()
        .antMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-resources/**").permitAll()
        .antMatchers("/ws/**").permitAll()
        .anyRequest().authenticated()

        .and()
        .exceptionHandling().accessDeniedHandler(new CustomAccessDeniedHandler())

        .and()
        .addFilterBefore(this.authenticationFilter, UsernamePasswordAuthenticationFilter.class)

        // OAuth2 로그인 구성
        .oauth2Login()
        .successHandler(oAuth2LoginSuccessHandler)
        .userInfoEndpoint().userService(oAuth2UserService);

    // Form 로그인 구성
    http.formLogin()
        .loginPage("/member/login")
        .failureHandler(getFailureHandler())
        .permitAll();

    // 로그아웃 구성
    http.logout()
        .logoutRequestMatcher(new AntPathRequestMatcher("/member/logout"))
        .logoutSuccessUrl("/")
        .invalidateHttpSession(true);
  }

  @Override
  public void configure(final WebSecurity web) throws Exception {
    web.ignoring()
        .antMatchers("/mysql-console/**", "/v2/api-docs", "/configuration/ui",
            "/swagger-resources", "/configuration/security",
            "/swagger-ui.html", "/webjars/**", "/swagger/**", "/h2-console");
  }

  @Bean
  public AuthenticationManager authenticationManager(
      AuthenticationConfiguration authenticationConfiguration)
      throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }
}

