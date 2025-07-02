package com.board.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class KakaoProfile {
  private Long platform_id;
  private String email;
  private String name;
  private String nickname;
}
