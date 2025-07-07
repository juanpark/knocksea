package com.board.auth.service;

import com.board.auth.CustomUserDetails;
import com.board.auth.jwt.JwtToken;
import com.board.auth.jwt.JwtTokenProvider;
import com.board.domain.Member;
import com.board.domain.Platform;
import com.board.domain.Token;
import com.board.dto.JwtTokenRequest;
import com.board.dto.JwtTokenResponse;
import com.board.dto.KakaoOauthResponse;
import com.board.dto.KakaoProfile;
import com.board.dto.MessageResponse;
import com.board.dto.UserLogin;
import com.board.dto.UserRegister;
import com.board.repository.MemberRepository;
import com.board.repository.TokenRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

/*
 * AuthService
 * 사용자 이메일 로그인&회원가입
 * 카카오 로그인
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
  private final RestTemplate restTemplate = new RestTemplate();
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Value("${kakao.client-id}")
  private String clientId;

  @Value("${kakao.redirect-uri}")
  private String redirectUri;

  // 이메일 로그인
  @Transactional
  public JwtTokenResponse localLogin(UserLogin userLogin) {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(userLogin.getEmail(), userLogin.getPassword()));

    return createTokenAndSave(authentication, "로그인 완료");
  }

  // 이메일 회원가입
  @Transactional
  public String register(UserRegister userRegister) {
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

  // 로그아웃 시 RefreshToken 삭제
  @Transactional
  public MessageResponse removeRefreshToken(String email) {
    Member member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));

    tokenRepository.deleteByMemberId(member.getId());
    return MessageResponse.builder().message("로그아웃 완료").build();
  }

  // AccessToken 재발급 (RefreshToken은 만료시 자동 로그아웃하여 다시 재로그인하도록 함)
  public JwtTokenResponse reissueAccessToken(JwtTokenRequest jwtTokenRequest) {
    String refreshToken = jwtTokenRequest.getRefreshToken();

    // Token 유효성 검증
    if (!jwtTokenProvider.validateToken(refreshToken)) {
      throw new RuntimeException("유효하지 않은 RefreshToken입니다.");
    }

    String email = jwtTokenProvider.extractEmail(refreshToken);

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

  /*kakao 로그인*/
  @Transactional
  public JwtTokenResponse kakaoOauthLogin(String code) {
    // 인가코드를 이용한 액세스 토큰 발급
    KakaoOauthResponse kakaoOauthResponse = kakaoAccessToken(code);
    // 액세스 토큰 이용해 카카오 프로필 정보(이름, 이메일, 닉네임) 받기
    KakaoProfile kakaoProfile = getKakaoProfile(kakaoOauthResponse.getAccess_token());

    // 회원 여부 확인(가입이 안된 회원일 경우 자동 회원가입 진행)
    Member member = memberRepository.findByEmail(kakaoProfile.getEmail())
        .orElseGet(() -> memberRepository.save(Member.builder()
            .email(kakaoProfile.getEmail())
            .nickname(kakaoProfile.getNickname())
            .name(kakaoProfile.getName())
            .password(null)
            .platform(Platform.KAKAO)
            .platformId(kakaoProfile.getPlatform_id().toString())
            .build()));

    CustomUserDetails userDetails = new CustomUserDetails(member);
    Authentication authentication = new UsernamePasswordAuthenticationToken(
        userDetails, null, userDetails.getAuthorities());

    SecurityContextHolder.getContext().setAuthentication(authentication);
    return createTokenAndSave(authentication, "카카오 로그인 완료");
  }

  // Token 생성 및 저장
  private JwtTokenResponse createTokenAndSave(Authentication authentication, String message) {
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
        .accessToken(jwtToken.getAccessToken())
        .refreshToken(jwtToken.getRefreshToken())
        .message(message)
        .build();
  }

  // 인가 코드 이용한 토큰 발급 요청
  private KakaoOauthResponse kakaoAccessToken(String code) {
    String tokenUrl = "https://kauth.kakao.com/oauth/token";

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    String body = "grant_type=authorization_code&"
        + "code=" + code
        + "&client_id=" + clientId
        + "&redirect_uri=" + redirectUri;

    HttpEntity<String> request = new HttpEntity<>(body, headers);
    ResponseEntity<String> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, request,
        String.class);
    log.info("[AuthService] kakaoOauthLogin response: {}", response.getBody());

    try {
      return objectMapper.readValue(response.getBody(), KakaoOauthResponse.class);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("카카오 토큰 응답 파싱 실패", e);
    }
  }

  // 액세스 토큰 이용한 사용자 프로필 정보 받기
  private KakaoProfile getKakaoProfile(String accessToken) {
    String profileUrl = "https://kapi.kakao.com/v2/user/me";

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    // 액세스 토큰 이용한 사용자 정보 GET 요청
    HttpEntity<String> request = new HttpEntity<>("", headers);
    ResponseEntity<String> response = restTemplate.exchange(profileUrl, HttpMethod.GET, request,
        String.class);
    log.info("[AuthService] getKakaoProfile response: {}", response.getBody());

    // 사용자 저장을 위한 사용자 정보 파싱
    try {
      JsonNode kakaoProfile = objectMapper.readTree(response.getBody());

      log.info("[AuthService] getKakaoProfile response: {}", kakaoProfile);

      return KakaoProfile.builder()
          .platform_id(kakaoProfile.get("id").asLong())
          .email(kakaoProfile.get("kakao_account").get("email").asText())
          .name(kakaoProfile.get("properties").get("nickname").asText())
          .nickname(kakaoProfile.get("properties").get("nickname").asText())
          .build();

    } catch (JsonProcessingException e) {
      throw new RuntimeException("카카오 프로필 응답 파싱 실패", e);
    }
  }
}
