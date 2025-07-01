package com.board.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class KakaoOauthResponse {
  private String access_token;
  private String token_type;
  private String refresh_token;
  private int expires_in;
  private String scope;
  private int refresh_token_expires_in;

}
