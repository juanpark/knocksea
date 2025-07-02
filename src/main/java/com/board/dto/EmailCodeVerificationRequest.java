package com.board.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EmailCodeVerificationRequest {

  private String email;
  private String code;

}
