package com.board.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CodeVerificationRequest {
  private String email;
  private String code;
}
