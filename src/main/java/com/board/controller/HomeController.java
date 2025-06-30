package com.board.controller;

import com.board.auth.CustomUserDetails;
import com.board.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Slf4j
//@RequestMapping("/api")
public class HomeController {
  @Value("${kakao.client-id}")
  private String clientId;

  @Value("${kakao.redirect-uri}")
  private String redirectUri;

  @GetMapping("/")
  public String home(@CookieValue(value="accessToken",required = false) String accessToken,
      @CookieValue(value="refreshToken",required = false) String refreshToken,
      Model model){
    if (accessToken != null) {
      model.addAttribute("accessToken", accessToken);
      model.addAttribute("refreshToken", refreshToken);
      // 필요하다면 토큰으로 사용자 정보 조회 등도 가능
    } else {
      model.addAttribute("message", "로그인이 필요합니다.");
    }
    return "home";
  }

  @GetMapping("/kakaologin")
  public String kakaologin(Model model) {
    String location = "https://kauth.kakao.com/oauth/authorize?client_id="+clientId+"&redirect_uri="+redirectUri+"&response_type=code&scope=account_email,profile_nickname";
    model.addAttribute("location",location);
    return "kakaologin";
  }

  // security 설정된 경로 요청
  // AuthenticationPrincipal은 현재 Authorization 헤더로 넘어온 토큰에 저장된 member을 반환합니다
  @PostMapping("/test")
  @ResponseBody
  public ResponseEntity<Member> test(@AuthenticationPrincipal CustomUserDetails userDetails) {
    Member member = userDetails.getMember();
    return ResponseEntity.ok(member);
  }

  @GetMapping("/test")
  public String getTest(){
    return "test";
  }
}
