package com.board.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class Member {
  // PK
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // 회원 아이디
  @Column(nullable = false, length = 255, unique = true)
  private String email;

  // 회원 이름
  @Column(nullable = false, length = 50)
  private String name;

  // 회원 닉네임 ( 작성자로 쓰임 )
  @Column(nullable = false, length = 50, unique = true)
  private String nickname;

  // 회원 패스워드
  @Column(length = 255)
  private String password;

  //KAKAO 로그인 시 사용자 고유 ID
  private String platformId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @ColumnDefault("'LOCAL'")
  private Platform platform;


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