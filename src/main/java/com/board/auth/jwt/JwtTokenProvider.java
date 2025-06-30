package com.board.auth.jwt;

import com.board.auth.CustomUserDetails;
import com.board.domain.Member;
import com.board.repository.MemberRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/*
 * JWT 발급, 검증, 이메일 파싱 등 핵심 로직
 * */
@Component
public class JwtTokenProvider {
  private final Key secretKey;
  private final long accessTokenValidityTime;
  private final long refreshTokenValidityTime;
  private final MemberRepository memberRepository;

  public JwtTokenProvider(
      @Value("${jwt.secret}") String secretKey,
      @Value("${jwt.access-token-validity-time}") long accessTokenValidityTime,
      @Value("${jwt.refresh-token-validity-time}") long refreshTokenValidityTime,
      MemberRepository memberRepository
  ){
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    this.accessTokenValidityTime = accessTokenValidityTime;
    this.refreshTokenValidityTime = refreshTokenValidityTime;
    this.memberRepository = memberRepository;
  }

  // AccessToken, RefreshToken 생성
  public JwtToken generateToken(Authentication authentication) {
    String accessToken = this.generateAccessToken(authentication);
    String refreshToken = this.generateRefreshToken(authentication);

    return JwtToken.builder()
        .grantType("Bearer")
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .build();
  }

  // AccessToken 생성
  public String generateAccessToken(Authentication authentication) {
    String authorities = authentication.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.joining(","));

    long now = System.currentTimeMillis();

    return Jwts.builder()
        .setHeader(createHeaders())
        .setSubject(authentication.getName()) // 사용자 아이디 설정
        .claim("iss","off")
        .claim("auth",authorities) //권한 설정
        .setExpiration(new Date(now + accessTokenValidityTime)) // 유효기간 1시간
        .setIssuedAt(new Date(now))
        .signWith(secretKey, SignatureAlgorithm.HS512)
        .compact();
  }

  // RefreshToken 생성
  private String generateRefreshToken(Authentication authentication) {
    String authorities = authentication.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.joining(","));

    long now = System.currentTimeMillis();
    Date refreshTokenExpired = new Date(now + refreshTokenValidityTime ); // 유효기간 15일

    return Jwts.builder()
        .setHeader(createHeaders())
        .setSubject(authentication.getName())
        .claim("iss","off")
        .claim("auth",authorities) //권한 설정
        .setExpiration(refreshTokenExpired)
        .setIssuedAt(new Date(now))
        .signWith(secretKey, SignatureAlgorithm.HS512)
        .compact();
  }

  //JWT에서 정보 추출해 Authentication 객체로 변환
  public Authentication getAuthentication(String token) {
    Claims claims = parseClaims(token);

    // 보안상 권한이 없는 사용자에 대한 인증을 막음
    if(claims.get("auth") == null){
      throw new RuntimeException("권한 정보가 없는 토큰입니다.");
    }

    Collection<? extends GrantedAuthority> authorities =
        Arrays.stream(claims.get("auth").toString().split(","))
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
    String email = claims.getSubject();

    Member member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("회원을 찾을 수 없습니다."));

    UserDetails principal = CustomUserDetails.builder().member(member).build();

    //SecurityContext에 저장되고 이후 로그인된 사용자처럼 동작하게 해줌
    return new UsernamePasswordAuthenticationToken(principal, "", authorities);
  }

  //JWT 유효성 검증
  public boolean validateToken(String token) {
    try{
      Jwts
          .parserBuilder()
          .setSigningKey(secretKey)
          .build()
          .parseClaimsJws(token);
      return true;
    } catch (SecurityException | MalformedJwtException e) {
      System.out.println("잘못된 JWT 서명입니다.");
    } catch (ExpiredJwtException e) {
      System.out.println("JWT 토큰이 만료되었습니다.");
    } catch (UnsupportedJwtException e) {
      System.out.println("지원되지 않는 JWT 토큰입니다.");
    } catch (IllegalArgumentException e) {
      System.out.println("JWT claims 문자열이 비어있습니다.");
    }

    return false;
  }

  // token 만료시간 추출
  public Date getExpiration(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(secretKey)
        .build()
        .parseClaimsJws(token)
        .getBody()
        .getExpiration();
  }

  public String extractEmail(String token){
    return Jwts.parserBuilder()
        .setSigningKey(secretKey)
        .build()
        .parseClaimsJws(token)
        .getBody()
        .getSubject();
  }

  /*
  JWT Payload 정보 파싱해 반환함
  만료된 경우에도 payload 정보 꺼내기 위해 try~catch문 사용
  */
  private Claims parseClaims(String token) {
    try{
      return Jwts.parserBuilder()
          .setSigningKey(secretKey)
          .build()
          .parseClaimsJws(token)
          .getBody();
    }catch(ExpiredJwtException e){
      return e.getClaims();
    }
  }

  //JWT Header 설정
  private Map<String, Object> createHeaders(){
    Map<String, Object> headers = new HashMap<>();
    headers.put("typ", "JWT");
    headers.put("alg", "HS512");
    return headers;
  }
}
