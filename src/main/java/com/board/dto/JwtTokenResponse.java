package com.board.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JwtTokenResponse {
  private String message;
  private String accessToken;
  private String refreshToken;
}
