package com.board.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserRegister {
  private String name;
  private String nickname;
  private String email;
  private String password;
}
