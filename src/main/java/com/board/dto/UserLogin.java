package com.board.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
public class UserLogin {

  @NotBlank(message = "이메일은 필수입니다.")
  @Pattern(regexp = "^[A-Za-z0-9._%+-]+@gmail\\.com$", message = "이메일은 @gmail.com으로 끝나야 합니다.")
  private String email;

  @NotBlank(message = "비밀번호를 입력해주세요.")
  @Size(min = 4, message = "비밀번호는 최소 4자 이상이어야 합니다.")
  private String password;
}
