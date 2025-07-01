package com.board.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

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
}
