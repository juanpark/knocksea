package com.board.auth;

import com.board.domain.Member;
import com.board.domain.Platform;
import java.util.Collection;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/*
* Spring Security가 인증처리에 사용하는 사용자 정보
* */
@Getter
@Builder
public class CustomUserDetails implements UserDetails {
  private final Member member;

  public CustomUserDetails(Member member) {
    this.member = member;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority("ROLE_USER"));
  }

  @Override
  public String getPassword() {

    //카카오 로그인 사용자일 경우 password는 null
    if(member.getPlatform() == Platform.KAKAO){
      return null;
    }else{
      return member.getPassword();
    }
  }

  @Override
  public String getUsername() {
    return member.getEmail();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
