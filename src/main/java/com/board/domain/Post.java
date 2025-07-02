package com.board.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postsId;

    @ManyToOne(fetch = FetchType.LAZY) //연관 엔티티 받을 때만 DB조회
    @JoinColumn(name = "user_id")
    private Member member;

    private String title;

    @Lob //Large Object(긴 텍스트 저장)
    private String content;

    private int likeCount;

    private int dislikeCount;

    @Enumerated(EnumType.STRING)
    private Status status = Status.WAITING; //기본 값

    private LocalDateTime createAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();

    private int viewCount;

    //Comment 엔티티의 post 필드가 주인 (외래 키를 가진 쪽이 주인)
    //게시글 저장,삭제 시 댓글도 저장,삭제 / 댓글 제거하면 DB에서도 삭제
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "posts_categories",
            joinColumns = @JoinColumn(name = "posts_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "posts_tags",
            joinColumns = @JoinColumn(name = "posts_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    //게시글 상태
    public enum Status {
        WAITING,
        COMPLETED,
        ADOPTED
    }

    //편의 메서드
    public void setMember(Member member) {
        this.member = member;
        if (!member.getPosts().contains(this)) {
            member.getPosts().add(this);
        }
    }

    //편의 메서드
    public void addComment(Comment comment) {
        comments.add(comment);
        comment.setPost(this);
    }
}
