package com.board.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EmailVerificationRequest {
  @NotBlank(message = "이메일은 필수입니다.")
  @Pattern(regexp = "^[A-Za-z0-9._%+-]+@gmail\\.com$", message = "이메일은 @gmail.com으로 끝나야 합니다.")
  private String email;
}
