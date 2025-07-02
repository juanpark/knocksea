package com.board.controller;

import com.board.auth.CustomUserDetails;
import com.board.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * Spring Security 설정된 경로
 * /users/* 경로는 모두 인증받은 사용자만 접근 가능
 * */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/users")
public class MemberController {

  // security 설정된 경로 요청
  // AuthenticationPrincipal은 현재 Authorization 헤더로 넘어온 토큰에 저장된 member을 반환합니다
  // GET /users
  @GetMapping
  public ResponseEntity<Member> findByMember(
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    Member member = userDetails.getMember();
    return ResponseEntity.ok(member);
  }
}
