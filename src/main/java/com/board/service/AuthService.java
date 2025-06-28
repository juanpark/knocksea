package com.board.service;

import com.board.auth.CustomUserDetails;
import com.board.auth.jwt.JwtToken;
import com.board.auth.jwt.JwtTokenProvider;
import com.board.domain.Member;
import com.board.domain.Platform;
import com.board.domain.Token;
import com.board.dto.UserLogin;
import com.board.dto.UserLoginResponse;
import com.board.dto.UserRegister;
import com.board.repository.MemberRepository;
import com.board.repository.TokenRepository;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
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
  private final PasswordEncoder passwordEncoder;

  // 로컬 로그인
  public UserLoginResponse localLogin(UserLogin userLogin) {
    log.info("[AuthService] localLogin");
    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
        userLogin.getEmail(), userLogin.getPassword());
    Authentication authentication = authenticationManager.authenticate(
        usernamePasswordAuthenticationToken);
    JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);
    LocalDateTime expiredAt = jwtTokenProvider.getExpiration(jwtToken.getRefreshToken())
        .toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime();
    log.info("[AuthService] localLogin authentication: {}",((CustomUserDetails) authentication.getPrincipal()).getMember());
    log.info("[AuthService] localLogin jwtToken: {}",jwtToken.getRefreshToken());
    Token token = Token.builder()
        .member(((CustomUserDetails) authentication.getPrincipal()).getMember())
        .refreshToken(jwtToken.getRefreshToken())
        .expiredAt(expiredAt)
        .build();

    tokenRepository.save(token);
    return UserLoginResponse.builder()
        .email(token.getMember().getEmail())
        .accessToken(jwtToken.getAccessToken())
        .build();
  }

  // 로컬 회원가입
  public String register(UserRegister userRegister){
    log.info("[AuthService] register");

    if(memberRepository.findByEmail(userRegister.getEmail()).isPresent()){
      throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
    }else{
      Member member = Member.builder()
          .name(userRegister.getName())
          .nickname(userRegister.getNickname())
          .email(userRegister.getEmail())
          .password(passwordEncoder.encode(userRegister.getPassword()))
          .platform(Platform.LOCAL)
          .platformId(null)
          .build();
      memberRepository.save(member);
      return member.getEmail();
    }
  }
}
