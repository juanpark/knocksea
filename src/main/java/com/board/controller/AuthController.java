package com.board.controller;

import com.board.auth.CustomUserDetails;
import com.board.dto.JwtTokenRequest;
import com.board.dto.JwtTokenResponse;
import com.board.dto.LogoutResponse;
import com.board.dto.UserLogin;
import com.board.dto.UserRegister;
import com.board.service.AuthService;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {
  private final AuthService authService;

  @PostMapping("/login")
  public ResponseEntity<JwtTokenResponse> localLogin(@RequestBody UserLogin userLogin){
    JwtTokenResponse response = authService.localLogin(userLogin);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @PostMapping("/register")
  public ResponseEntity<String> localRegister(@RequestBody UserRegister userRegister){
    try{
      String email = authService.register(userRegister);
      return new ResponseEntity<>(email, HttpStatus.CREATED);
    }catch(IllegalArgumentException e){
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
  }

  @PostMapping("/logout")
  public ResponseEntity<LogoutResponse> localLogout(@AuthenticationPrincipal CustomUserDetails userDetails){
    if(userDetails == null){
      LogoutResponse response = LogoutResponse.builder().message("잘못된 토큰 형식입니다.").build();
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    String email = userDetails.getUsername();
    LogoutResponse response = authService.removeRefreshToken(email);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @PostMapping("/reissue")
  public ResponseEntity<JwtTokenResponse> reissueAccessToken(@RequestBody JwtTokenRequest jwtTokenRequest){
    JwtTokenResponse response = authService.reissueAccessToken(jwtTokenRequest);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  /*카카오 OAuth2 로그인*/
  /*클라이언트에서 인가코드를 전달받은 뒤 AccessToken, RefreshToken 발급, 사용자 저장 */
  @GetMapping("oauth/kakao/login")
  public ResponseEntity<JwtTokenResponse>  kakaoOauthLogin(@RequestParam("code") String code){
    JwtTokenResponse response = authService.kakaoOauthLogin(code);
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + response.getAccessToken());

    return ResponseEntity.ok().headers(headers).body(response);
  }
}
