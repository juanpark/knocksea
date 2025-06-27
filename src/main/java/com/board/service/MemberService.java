package com.board.service;

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

}
