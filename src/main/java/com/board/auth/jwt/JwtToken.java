package com.board.auth.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class JwtToken {
    private String accessToken;
    private String refreshToken;
    private String grantType;
}
