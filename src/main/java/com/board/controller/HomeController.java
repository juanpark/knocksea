package com.board.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

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


}
