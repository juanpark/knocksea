package com.board.controller;

import com.board.auth.service.AuthService;
import com.board.dto.JwtTokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Slf4j
@RequiredArgsConstructor
public class KakaoController {

  private final AuthService authService;

  /*카카오 OAuth2 로그인*/
  /*클라이언트에서 인가코드를 전달받은 뒤 AccessToken, RefreshToken 발급, 사용자 저장 */
  @GetMapping("oauth/kakao/login")
  public String kakaoOauthLogin(@RequestParam("code") String code, Model model) {
    JwtTokenResponse jwtTokenResponse = authService.kakaoOauthLogin(code);
    model.addAttribute("accessToken", jwtTokenResponse.getAccessToken());
    model.addAttribute("refreshToken", jwtTokenResponse.getRefreshToken());

    return "token-handler";
  }
}
