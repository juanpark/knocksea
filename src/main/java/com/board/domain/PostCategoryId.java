package com.board.domain;

import java.io.Serializable;

import jakarta.persistence.Embeddable;

@Embeddable
public class PostCategoryId implements Serializable {
    private Long postsId;
    private Long categoryId;

    // 기본 생성자, equals, hashCode 필요
}
