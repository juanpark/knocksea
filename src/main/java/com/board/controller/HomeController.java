package com.board.controller;

import com.board.dto.UserLogin;
import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;


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
    model.addAttribute("content", "home");
    return "home";
  }

  @GetMapping("/test")
  public String test() {
    return "test";
  }

  @GetMapping("/login")
  public String login(Model model, @ModelAttribute("errors") String errors) {
    model.addAttribute("userLogin", new UserLogin("","")); // 빈 폼 객체 제공
    log.info(errors.getClass().getName());
    return "login";
  }

  @GetMapping("/connect-kakao")
  public ResponseEntity<Void> connectKakao() {
    String location =
        "https://kauth.kakao.com/oauth/authorize?client_id=" + clientId + "&redirect_uri="
            + redirectUri + "&response_type=code&scope=account_email,profile_nickname";

    HttpHeaders headers = new HttpHeaders();
    headers.setLocation(URI.create(location));
    return new ResponseEntity<>(headers, HttpStatus.FOUND); // 302 Redirect
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
