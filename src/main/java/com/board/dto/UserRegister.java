package com.board.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserRegister {

  @NotBlank(message = "이름은 필수입니다.")
  private String name;

  @NotBlank(message = "닉네임은 필수입니다.")
  @Size(max = 10, message = "닉네임은 10자 이하여야 합니다.")
  private String nickname;

  @NotBlank(message = "이메일은 필수입니다.")
  @Pattern(regexp = "^[A-Za-z0-9._%+-]+@gmail\\.com$", message = "이메일은 @gmail.com으로 끝나야 합니다.")
  private String email;

  @NotBlank(message = "비밀번호는 필수입니다.")
  @Size(min = 4, message = "비밀번호는 최소 4자 이상이어야 합니다.")
  private String password;
}
