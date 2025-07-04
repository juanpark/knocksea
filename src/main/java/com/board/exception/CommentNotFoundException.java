package com.board.exception;

import org.springframework.http.HttpStatus;

public class CommentNotFoundException extends CustomException {
    public CommentNotFoundException() {
        super("댓글이 존재하지 않습니다.", HttpStatus.NOT_FOUND); // 404
    }
}
