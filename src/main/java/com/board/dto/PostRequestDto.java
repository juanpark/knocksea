package com.board.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PostRequestDto {

    private Long userId;
    private String title;
    private String content;
    private Long categoryId;
    private List<String> tagNames;
}
