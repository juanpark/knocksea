package com.board.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedCommentAccessException extends CustomException {
    public UnauthorizedCommentAccessException() {
        super("해당 댓글에 대한 권한이 없습니다.", HttpStatus.FORBIDDEN); // 403
    }
}
