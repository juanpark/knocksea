package com.board.dto;

import lombok.Getter;
import lombok.Setter;

// 댓글 생성 시 요청 바디
@Getter
@Setter
public class CommentCreateRequest {

    private Long postId;      // 어떤 게시글에 달린 댓글인지
    private Long userId;      // 작성자 ID
    private String content;   // 댓글 내용
    private Long parentId;    // 대댓글인 경우 부모 댓글 ID (nullable)
}