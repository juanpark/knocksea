package com.board.exception;

public class UnauthorizedCommentAdoptException extends SecurityException {
    public UnauthorizedCommentAdoptException() {
        super("댓글 채택 권한이 없습니다.");
    }
}