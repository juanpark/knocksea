package com.board.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


/*
 * 템플릿 파일 호출 목적 Controller
 * Security 설정 X
 * 클래스 이름이나 경로 이름은 추후 변경
 * */
@Controller
@Slf4j
public class HomeController {

  @Value("${kakao.client-id}")
  private String clientId;

  @Value("${kakao.redirect-uri}")
  private String redirectUri;

  @Value("${google.secret}")
  private String googleMapKey;

  @GetMapping("/")
  public String home(Model model) {
    return "home";
  }

  @GetMapping("/test")
  public String test() {
    return "test";
  }

  @GetMapping("/login")
  public String login(Model model) {
    String location =
        "https://kauth.kakao.com/oauth/authorize?client_id=" + clientId + "&redirect_uri="
            + redirectUri + "&response_type=code&scope=account_email,profile_nickname";
    model.addAttribute("location", location);
    return "login";
  }

  @GetMapping("/register")
  public String register() {
    return "register";
  }

  @GetMapping("/map")
  public String map(Model model) {
    model.addAttribute("googleMapKey", googleMapKey);
    return "map";
  }
}
