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
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
  public ResponseEntity<MessageResponse> checkNickname(@RequestParam String nickname) {
    boolean exists = memberService.checkNickname(nickname);
    String msg = exists ? "중복된 닉네임입니다." : "사용 가능한 닉네임입니다.";
    return ResponseEntity.ok(MessageResponse.builder().message(msg).build());
  }

  // 이메일 인증 코드 요청 (중복 이메일 검증 후 인증코드 전송)
  @PostMapping("/email-verification")
  @ResponseBody
  public ResponseEntity<MessageResponse> sendVerification(
      @Valid @RequestBody EmailVerificationRequest emailVerificationRequest) {

    MessageResponse emailResponse = memberService.sendVerification(emailVerificationRequest.getEmail());
    return new ResponseEntity<>(emailResponse, HttpStatus.OK);
  }

  // 인증코드 검증
  @PostMapping("/email-verification/verify")
  @ResponseBody
  public ResponseEntity<MessageResponse> codeVerification(
      @RequestBody EmailCodeVerificationRequest emailCodeVerificationRequest) {
    MessageResponse emailResponse = memberService.verifyEmailCode(
        emailCodeVerificationRequest.getEmail(), emailCodeVerificationRequest.getCode());

    return new ResponseEntity<>(emailResponse, HttpStatus.OK);
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
  public String localLogin(@ModelAttribute @Valid UserLogin userLogin, BindingResult bindingResult,
      RedirectAttributes redirectAttributes,Model model) {

    if (bindingResult.hasErrors()) {
      String errorMessage = bindingResult.getFieldErrors().stream()
          .map(FieldError::getDefaultMessage)  // "이메일은 @gmail.com으로 끝나야 합니다." 만 추출
          .collect(Collectors.joining("<br/>"));
      redirectAttributes.addFlashAttribute("errors", errorMessage);
      return "redirect:/login"; // 다시 로그인 페이지로
    }

    try {
      JwtTokenResponse response = authService.localLogin(userLogin);
      model.addAttribute("accessToken", response.getAccessToken());
      model.addAttribute("refreshToken", response.getRefreshToken());
      return "token-handler";
    } catch (BadCredentialsException e) {
      redirectAttributes.addFlashAttribute("errors", "아이디 또는 비밀번호가 올바르지 않습니다.");
      return "redirect:/login";
    }
  }

  // 이메일 회원가입
  // POST /register
  @PostMapping("/register")
  @ResponseBody
  public ResponseEntity<String> localRegister(@Valid @RequestBody UserRegister userRegister) {
      log.info("localRegister userRegister: {}", userRegister.toString());
      String email = authService.register(userRegister);
      return new ResponseEntity<>(email, HttpStatus.CREATED);
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
