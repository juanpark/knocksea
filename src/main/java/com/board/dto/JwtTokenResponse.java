package com.board.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtTokenResponse {
  @JsonProperty("message")
  private String message;
  @JsonProperty("accessToken")
  private String accessToken;
  @JsonProperty("refreshToken")
  private String refreshToken;
}
