package com.board.auth.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider jwtTokenProvider;
  private final List<String> excludedPaths = List.of("/login","/signup","/register");

  //필터 제외할 경로 설정
  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
    String requestURI = request.getRequestURI();
    log.info("[JwtAuthenticationFilter shouldNotFilter] 현재 요청한 URI : {}",requestURI);
    return excludedPaths.stream().anyMatch(requestURI::startsWith);
  }

  //요청의 Authorization Header 헤더에서 토큰 추출
  private String resolveToken(HttpServletRequest request) {
    String bearer = request.getHeader("Authorization");
    if(bearer != null && bearer.startsWith("Bearer ")) {
      log.info("[JwtAuthenticationFilter resolveToken] bearer: {}", bearer);
      return bearer.substring(7);
    }
    return null;
  }

  // JWT 토큰 검사하고 인증 정보를 SecurityContext에 저장
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    log.info("Authorization: {}", request.getHeader("Authorization"));
    String token = resolveToken(request);
    log.info("[JwtAuthenticationFilter doFilterInternal] JWT token: {}", token);

    if(token != null) {
      try{
        if(jwtTokenProvider.validateToken(token)) {
          Authentication authentication = jwtTokenProvider.getAuthentication(token);
          SecurityContextHolder.getContext().setAuthentication(authentication);
          log.info("[JwtAuthenticationFilter doFilterInternal] 사용자 정보: {}", authentication.getPrincipal());
        }
      }catch(Exception e){
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().println(e.getMessage());
        return;
      }
    }
    filterChain.doFilter(request, response);
  }
}
