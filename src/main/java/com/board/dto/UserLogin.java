package com.board.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserLogin {
  private String email;
  private String password;
}
