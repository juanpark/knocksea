package com.board.controller;

import com.board.auth.service.AuthService;
import com.board.auth.CustomUserDetails;
import com.board.dto.EmailCodeVerificationRequest;
import com.board.dto.EmailVerificationRequest;
import com.board.dto.JwtTokenRequest;
import com.board.dto.JwtTokenResponse;
import com.board.dto.MessageResponse;
import com.board.dto.UserLogin;
import com.board.dto.UserRegister;
import com.board.service.MailService;
import com.board.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
  private final MailService mailService;

  // 닉네임 중복 확인
  @GetMapping("/check-nickname")
  @ResponseBody
  public ResponseEntity<Boolean> checkNickname(@RequestParam String nickname) {
    return new ResponseEntity<>(!memberService.checkNickname(nickname), HttpStatus.OK);
  }

  // 이메일 인증 코드 요청 (중복이메일 검증 후 인증코드 전송)
  @PostMapping("/email-verification")
  @ResponseBody
  public ResponseEntity<MessageResponse> sendVerification(
      @RequestBody EmailVerificationRequest emailVerificationRequest) {
    MessageResponse emailResponse;
    try {
      if (memberService.findByEmail(emailVerificationRequest.getEmail()) != null) {
        emailResponse = MessageResponse.builder().message("중복된 이메일입니다.").build();
        return new ResponseEntity<>(emailResponse, HttpStatus.BAD_REQUEST);
      } else {
        emailResponse = mailService.sendVerificationCode(emailVerificationRequest.getEmail());
        return new ResponseEntity<>(emailResponse, HttpStatus.OK);
      }
    } catch (Exception e) {
      emailResponse = MessageResponse.builder().message(e.getMessage()).build();
      return new ResponseEntity<>(emailResponse, HttpStatus.BAD_REQUEST);
    }
  }

  // 인증코드 검증
  @PostMapping("/email-verification/verify")
  @ResponseBody
  public ResponseEntity<MessageResponse> codeVerification(
      @RequestBody EmailCodeVerificationRequest emailCodeVerificationRequest) {
    MessageResponse emailResponse;
    try {
      if (mailService.codeVerification(emailCodeVerificationRequest.getEmail(),
          emailCodeVerificationRequest.getCode())) {
        emailResponse = MessageResponse.builder().message("이메일 인증 성공").build();
        return new ResponseEntity<>(emailResponse,HttpStatus.OK);
      } else {
        emailResponse = MessageResponse.builder().message("이메일 인증 실패").build();
        return new ResponseEntity<>(emailResponse, HttpStatus.OK);
      }
    } catch (Exception e) {
      emailResponse = MessageResponse.builder().message(e.getMessage()).build();
      return new ResponseEntity<>(emailResponse, HttpStatus.BAD_REQUEST);
    }
  }

  // 인증코드 만료
  @PostMapping("/email-verification/expire")
  @ResponseBody
  public ResponseEntity<String> expireCode(@RequestBody EmailVerificationRequest emailVerificationRequest) {
    mailService.expireCode( emailVerificationRequest.getEmail());
    return ResponseEntity.ok("인증 코드가 만료되었습니다.");
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

  // 이메일 회원가입
  // POST /register
  @PostMapping("/register")
  @ResponseBody
  public ResponseEntity<String> localRegister(@Valid @RequestBody UserRegister userRegister) {
    try {
      log.info("localRegister userRegister: {}", userRegister.toString());
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
  public ResponseEntity<MessageResponse> localLogout(
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    if (userDetails == null) {
      MessageResponse response = MessageResponse.builder().message("잘못된 토큰 형식입니다.").build();
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    String email = userDetails.getUsername();
    MessageResponse response = authService.removeRefreshToken(email);
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
