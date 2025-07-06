package com.board.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

// 전역 예외 처리용 응답 DTO
@Getter
@AllArgsConstructor
@Builder
public class ErrorResponse {

    private final LocalDateTime timestamp; // 예외 발생 시간
    private final int status;              // HTTP 상태 코드 (e.g., 400)
    private final String error;            // 상태 코드 텍스트 (e.g., "Bad Request")
    private final String message;          // 사용자에게 보여줄 예외 메시지
}