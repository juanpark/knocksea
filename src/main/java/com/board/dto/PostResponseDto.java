package com.board.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class PostResponseDto {

    private Long postsId;
    private Long userId; // 댓글 사용자 인증을 위해 추가
    private String userName;
    private String title;
    private String content;
    private int likeCount;
    private int dislikeCount;
    private String status;
    private int viewCount;
    private int commentCount; // 댓글 개수 표현을 위해 추가
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<String> categoryNames;
    private List<String> tagNames;
}
