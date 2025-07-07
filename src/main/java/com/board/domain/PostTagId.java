package com.board.domain;

import java.io.Serializable;

import jakarta.persistence.Embeddable;

@Embeddable
public class PostTagId implements Serializable {
    private Long postsId;
    private Long tagId;

    // 기본 생성자, equals, hashCode 필요
}
