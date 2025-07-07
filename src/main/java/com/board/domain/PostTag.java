package com.board.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "posts_tags")
public class PostTag {
	@EmbeddedId
    private PostTagId id = new PostTagId();
	
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("postsId")
    @JoinColumn(name = "posts_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("tagId")
    @JoinColumn(name = "tag_id")
    private Tag tag;
}
