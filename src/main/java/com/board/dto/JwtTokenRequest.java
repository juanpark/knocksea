package com.board.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JwtTokenRequest {
  private String refreshToken;
}
