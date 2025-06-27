package com.board.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserLoginResponse {
  private String email;
  private String refreshToken;
}
