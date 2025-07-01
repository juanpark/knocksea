package com.board.auth.service;

import com.board.auth.CustomUserDetails;
import com.board.domain.Member;
import com.board.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/*
 * CustomUserDetailsService
 * 로그인 시 사용자가 입력한 이메일 기준
 * DB에서 사용자 정보 조회해 UserDetails 객체로 반환하는 역할 수행
 * */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final MemberRepository memberRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    Member member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("회원을 찾을 수 없습니다."));
    ;

    if (member == null) {
      throw new UsernameNotFoundException("해당 사용자를 찾을 수 없습니다: " + email);
    } else {
      return CustomUserDetails.builder()
          .member(member)
          .build();
    }
  }
}
