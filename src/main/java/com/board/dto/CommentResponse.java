package com.board.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

// 댓글 조회 결과 응답 (계층 구조 포함)
@Getter
@Setter
public class  CommentResponse {

    private Long commentId;
    private Long userId;             // 작성자 ID (Member 엔티티 기준)
    private String nickname;         // 작성자 닉네임 (Member 엔티티 기준)
    private String content;

    private boolean answer;
    private int likeCount;
    private int dislikeCount;

    private LocalDateTime createAt;
    private LocalDateTime updatedAt;

    private Long parentId;           // 대댓글인 경우 부모 ID

    // 계층 구조 표현을 위해 children 포함 → 재귀 구조로 서비스에서 처리
    private List<CommentResponse> children; // 대댓글 리스트
}