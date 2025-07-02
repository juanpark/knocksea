package com.board.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EmailVerificationRequest {
  private String email;
}
