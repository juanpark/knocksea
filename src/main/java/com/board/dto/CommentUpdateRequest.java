package com.board.dto;

import lombok.Getter;
import lombok.Setter;

// 댓글 수정 시 요청 바디
@Getter
@Setter
public class CommentUpdateRequest {

    private String content;  // 수정된 댓글 내용
}