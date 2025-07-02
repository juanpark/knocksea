package com.board.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MessageResponse {
  private String message;
}
