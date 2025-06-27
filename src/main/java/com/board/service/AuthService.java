package com.board.service;

import com.board.auth.CustomUserDetails;
import com.board.auth.jwt.JwtToken;
import com.board.auth.jwt.JwtTokenProvider;
import com.board.domain.Token;
import com.board.dto.UserLogin;
import com.board.dto.UserLoginResponse;
import com.board.repository.MemberRepository;
import com.board.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/*
 * AuthService
 * 사용자 로그인, 회원가입
 * JWT 토큰 발급, 재발급 관련 로직
 * */
@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

  private final AuthenticationManager authenticationManager;
  private final MemberRepository memberRepository;
  private final TokenRepository tokenRepository;
  private final JwtTokenProvider jwtTokenProvider;

  public UserLoginResponse localLogin(UserLogin userLoginDto) {
    log.info("[AuthService] localLogin");
    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
        userLoginDto.getEmail(), userLoginDto.getPassword());
    Authentication authentication = authenticationManager.authenticate(
        usernamePasswordAuthenticationToken);
    JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);
    log.info("[AuthService] localLogin authentication: {}",((CustomUserDetails) authentication.getPrincipal()).getMember());
    log.info("[AuthService] localLogin jwtToken: {}",jwtToken.getRefreshToken());
    Token token = Token.builder()
        .member(((CustomUserDetails) authentication.getPrincipal()).getMember())
        .refreshToken(jwtToken.getRefreshToken())
        .build();

    tokenRepository.save(token);
    return UserLoginResponse.builder()
        .email(token.getMember().getEmail())
        .refreshToken(token.getRefreshToken())
        .build();
  }
}
