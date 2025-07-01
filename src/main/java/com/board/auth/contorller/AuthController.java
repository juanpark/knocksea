package com.board.auth.contorller;

import com.board.auth.service.AuthService;
import com.board.auth.CustomUserDetails;
import com.board.dto.JwtTokenRequest;
import com.board.dto.JwtTokenResponse;
import com.board.dto.LogoutResponse;
import com.board.dto.UserLogin;
import com.board.dto.UserRegister;
import com.board.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AuthController {

  private final AuthService authService;
  private final MemberService memberService;

  // 닉네임 중복 확인
  @GetMapping("/check-nickname")
  @ResponseBody
  public ResponseEntity<Boolean> checkNickname(@RequestParam String nickname){
    return new ResponseEntity<>(!memberService.checkNickname(nickname), HttpStatus.OK);
  }

  // 자체 로그인
  // POST /login
  @PostMapping("/login")
  public String localLogin(@RequestParam String email,
      @RequestParam String password, Model model) {

    JwtTokenResponse response = authService
        .localLogin(UserLogin.builder().email(email).password(password).build());

    // 로그인 성공 후 localStorage에 token 저장 위한 token-handler.html 이동
    // localStorage 설정 뒤 홈으로 자동 이동
    model.addAttribute("accessToken", response.getAccessToken());
    model.addAttribute("refreshToken", response.getRefreshToken());
    return "token-handler";
  }

  // 자체 회원가입
  // POST /register
  @PostMapping("/register")
  @ResponseBody
  public ResponseEntity<String> localRegister(@RequestBody UserRegister userRegister) {
    try {
      String email = authService.register(userRegister);
      return new ResponseEntity<>(email, HttpStatus.CREATED);
    } catch (IllegalArgumentException e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
  }

  // 자체 로그아웃
  // POST /logout
  @PostMapping("/logout")
  @ResponseBody
  public ResponseEntity<LogoutResponse> localLogout(
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    if (userDetails == null) {
      LogoutResponse response = LogoutResponse.builder().message("잘못된 토큰 형식입니다.").build();
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    String email = userDetails.getUsername();
    LogoutResponse response = authService.removeRefreshToken(email);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  // AccessToken 재발급
  // POST /reissue
  @PostMapping("/reissue")
  @ResponseBody
  public ResponseEntity<JwtTokenResponse> reissueAccessToken(
      @RequestBody JwtTokenRequest jwtTokenRequest) {
    JwtTokenResponse response = authService.reissueAccessToken(jwtTokenRequest);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}
