package com.board.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "users")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String nickname;

    private String email;

    private String password;

    private String platformId;

    private String platform;

    //Post 엔티티의 user 필드가 주인 (외래 키를 가진 쪽이 주인)
    //게시글 저장,삭제 시 댓글도 저장,삭제 / 댓글 제거하면 DB에서도 삭제
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    //Comment 엔티티의 user 필드가 주인 (외래 키를 가진 쪽이 주인)
    //게시글 저장,삭제 시 댓글도 저장,삭제 / 댓글 제거하면 DB에서도 삭제
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    //편의 메서드
    public void addPost(Post post) {
        posts.add(post);
        post.setMember(this);
    }

    //편의 메서드
    public void addComment(Comment comment) {
        comments.add(comment);
        comment.setMember(this);
    }
}