package com.board.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. IllegalArgumentException 처리
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // 2-1. 권한 예외 처리 (접근 권한 없음)
    @ExceptionHandler({SecurityException.class, AccessDeniedException.class})
    public ResponseEntity<ErrorResponse> handleAccessDenied(Exception ex) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, "접근 권한이 없습니다.");
    }

    // 2-2. 권한 부족 (AccessDeniedException → 403 Forbidden)
    // Spring Security는 권한이 부족할 때 AccessDeniedException을 발생시킴 -> 메세지나 상황이 구체적이지 않음
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.FORBIDDEN.value())
                .error(HttpStatus.FORBIDDEN.getReasonPhrase())
                .message("❌ 접근 권한이 없습니다.")
                .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    // 3. 인증 예외 처리 (로그인 안 한 경우)
    @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAuthentication(AuthenticationCredentialsNotFoundException ex) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
    }

    // 4. 유효성 검증 실패 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + " : " + error.getDefaultMessage())
                .findFirst()
                .orElse("유효성 검사 실패");
        return buildErrorResponse(HttpStatus.BAD_REQUEST, message);
    }

    // 5. 파라미터 타입 불일치 처리
    @ExceptionHandler(org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(org.springframework.web.method.annotation.MethodArgumentTypeMismatchException ex) {
        String name = ex.getName();
        String requiredType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "알 수 없음";
        String message = "파라미터 '" + name + "'은(는) " + requiredType + " 타입이어야 합니다.";
        return buildErrorResponse(HttpStatus.BAD_REQUEST, message);
    }

    // 6. 그 외 모든 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "💥 서버 내부 오류가 발생했습니다.");
    }

    // 7. CustomException 공통 처리
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException ex) {
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(ex.getStatus().value())
                .error(ex.getStatus().getReasonPhrase())
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(ex.getStatus()).body(error);
    }

    // 공통 응답 생성 메서드
    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String message) {
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .build();
        return ResponseEntity.status(status).body(error);
    }
}