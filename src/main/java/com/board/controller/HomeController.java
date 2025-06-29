package com.board.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
//@RequestMapping("/api")
public class HomeController {

  @GetMapping("/")
  public String home() {
    log.info("[HomeController] URL /");
    return "home";
  }


}
