package com.board.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class PostResponseDto {

    private Long postsId;
    private String userName;
    private String title;
    private String content;
    private int likeCount;
    private int dislikeCount;
    private String status;
    private int viewCount;
    private LocalDateTime createAt;
    private LocalDateTime updatedAt;
    private List<String> categoryNames;
    private List<String> tagNames;
}
