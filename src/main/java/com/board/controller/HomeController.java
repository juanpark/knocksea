package com.board.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
//@RequestMapping("/api")
public class HomeController {
  @Value("${kakao.client-id}")
  private String clientId;

  @Value("${kakao.redirect-uri}")
  private String redirectUri;

  @GetMapping("/")
  public String home() {
    log.info("[HomeController] URL /");
    return "home";
  }

  @GetMapping("/kakaologin")
  public String kakaologin(Model model) {
    model.addAttribute("client_id",clientId);
    model.addAttribute("redirect_uri",redirectUri);
    return "kakaologin";
  }


}
