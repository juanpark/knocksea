package com.board.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @ManyToOne(fetch = FetchType.LAZY) //연관 엔티티 받을 때만 DB조회
    @JoinColumn(name = "posts_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Member member;

    // 대댓글 구조
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    //Comment 엔티티 안의 parent 필드가 주인 (외래 키를 가진 쪽이 주인)
    //게시글 저장,삭제 시 댓글도 저장,삭제 / 댓글 제거하면 DB에서도 삭제
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> children = new ArrayList<>();

    @Lob
    private String content;

    private int likeCount;

    private int dislikeCount;

    private boolean isAnswer = false;

    private LocalDateTime createAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();

    // Post 연관관계 편의 메서드
    public void setPost(Post post) {
        this.post = post;
        if (!post.getComments().contains(this)) {
            post.getComments().add(this);
        }
    }

    // Member 연관관계 편의 메서드
    public void setMember(Member member) {
        this.member = member;
        if (!member.getComments().contains(this)) {
            member.getComments().add(this);
        }
    }

    // 대댓글 연관관계 편의 메서드
    public void addChildComment(Comment child) {
        children.add(child);
        child.setParent(this);
    }

    public void setParent(Comment parent) {
        this.parent = parent;
        if (parent != null && !parent.getChildren().contains(this)) {
            parent.getChildren().add(this);
        }
    }
}