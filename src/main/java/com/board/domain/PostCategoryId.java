package com.board.domain;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Embeddable
public class PostCategoryId implements Serializable {
    private Long postsId;
    private Long categoryId;
}
