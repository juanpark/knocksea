package com.board.service;

import com.board.auth.CustomUserDetails;
import com.board.auth.jwt.JwtToken;
import com.board.auth.jwt.JwtTokenProvider;
import com.board.domain.Member;
import com.board.domain.Platform;
import com.board.domain.Token;
import com.board.dto.JwtTokenRequest;
import com.board.dto.JwtTokenResponse;
import com.board.dto.LogoutResponse;
import com.board.dto.UserLogin;
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
import org.springframework.transaction.annotation.Transactional;

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
  @Transactional
  public JwtTokenResponse localLogin(UserLogin userLogin) {
    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
        userLogin.getEmail(), userLogin.getPassword());

    Authentication authentication = authenticationManager.authenticate(
        usernamePasswordAuthenticationToken);
    JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);

    LocalDateTime expiredAt = jwtTokenProvider.getExpiration(jwtToken.getRefreshToken())
        .toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime();

    Token token = Token.builder()
        .member(((CustomUserDetails) authentication.getPrincipal()).getMember())
        .refreshToken(jwtToken.getRefreshToken())
        .expiredAt(expiredAt)
        .build();

    tokenRepository.save(token);

    return JwtTokenResponse.builder()
        .message("로그인 완료")
        .accessToken(jwtToken.getAccessToken())
        .refreshToken(jwtToken.getRefreshToken())
        .build();
  }

  // 로컬 회원가입
  @Transactional
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

  @Transactional
  public LogoutResponse removeRefreshToken(String email){
    log.info("[AuthService] removeRefreshToken email: {}", email);
    Long userId = memberRepository.findByEmail(email).get().getId();
    log.info("[AuthService] removeRefreshToken userId: {}", userId);
    tokenRepository.deleteByMemberId(userId);
    return LogoutResponse.builder().message("로그아웃 완료").build();
  }

  // AccessToken 재발급 (RefreshToken은 만료시 자동 로그아웃하여 다시 재로그인하도록 함)
  public JwtTokenResponse reissueAccessToken(JwtTokenRequest jwtTokenRequest){
    String refreshToken = jwtTokenRequest.getRefreshToken();

    // Token 유효성 검증
    if(!jwtTokenProvider.validateToken(refreshToken)){
      throw new RuntimeException("유효하지 않은 RefreshToken입니다.");
    }

    String email = jwtTokenProvider.extractEmail(refreshToken);
    log.info("[AuthService] reissueAccessToken email: {}", email);

    // 저장된 RefreshToken과 일치하는 지 확인
    Member member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));

    Token savedToken = tokenRepository.findByMemberId(member.getId())
        .orElseThrow(() -> new RuntimeException("저장된 토큰이 없습니다."));

    if (!savedToken.getRefreshToken().equals(refreshToken)) {
      throw new RuntimeException("위조된 RefreshToken입니다.");
    }

    // 새 AccessToken 발급
    Authentication authentication = jwtTokenProvider.getAuthentication(refreshToken);
    String newAccessToken = jwtTokenProvider.generateAccessToken(authentication);

    return JwtTokenResponse.builder()
        .message("AccessToken 재발급 완료")
        .accessToken(newAccessToken)
        .refreshToken(refreshToken)
        .build();
  }
}
