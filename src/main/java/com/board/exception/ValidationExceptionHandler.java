package com.board.exception;

import com.board.dto.ValidExceptionResponse;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/*
* 유효성 검사 실패 시 처리용 Exception handler
* */
@RestControllerAdvice
public class ValidationExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ValidExceptionResponse> handleValidationException(MethodArgumentNotValidException ex) {
    // BindingResult 통해 Validation에서 걸린 변수의 message 확인 가능
    Map<String, String> errorMap = new HashMap<>();

    for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
      errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
    }

    ValidExceptionResponse response = ValidExceptionResponse.builder()
        .message("Validation failed")
        .data(errorMap)
        .build();

    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

}
