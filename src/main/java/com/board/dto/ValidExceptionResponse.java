package com.board.dto;

import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ValidExceptionResponse {
  private String message;
  private Map<String, String> data;
}
