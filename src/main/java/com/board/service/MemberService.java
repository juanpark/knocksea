package com.board.service;

import com.board.domain.Member;
import com.board.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/*
 * MemberService
 * 사용자  조회, 삭제 역할
 * */
@Service
@RequiredArgsConstructor
public class MemberService {

  private final MemberRepository memberRepository;

  // 이름 및 닉네임 변경
  public Member updateMemberName(Member member) {
    return memberRepository.save(member);
  }

  // 사용자 정보 조회(이메일 기준)
  public Member findByEmail(String email) {
    return memberRepository.findByEmail(email).orElse(null);
  }
}
