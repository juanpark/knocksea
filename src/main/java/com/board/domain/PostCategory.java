package com.board.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "posts_categories")
public class PostCategory {
	@EmbeddedId
    private PostCategoryId id = new PostCategoryId();
	
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("postsId")
    @JoinColumn(name = "posts_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("categoryId")
    @JoinColumn(name = "category_id")
    private Category category;
}
