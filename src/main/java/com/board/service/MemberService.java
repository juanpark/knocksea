package com.board.service;

import com.board.domain.Member;
import com.board.dto.MessageResponse;
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
  private final MailService mailService;

  // 이름 및 닉네임 변경
  public Member updateMemberName(Member member) {
    return memberRepository.save(member);
  }

  // 사용자 정보 조회(이메일 기준)
  public Member findByEmail(String email) {
    return memberRepository.findByEmail(email).orElse(null);
  }

  // 사용자 닉네임 중복 확인
  public boolean checkNickname(String nickname){
    return memberRepository.existsByNickname(nickname);
  }

  //이메일 중복 확인 및 인증코드 전송
  public MessageResponse sendVerification(String email){
    if(this.findByEmail(email) != null){
      throw new IllegalArgumentException("중복된 이메일입니다.");
    }

    return mailService.sendVerificationCode(email);
  }

  // 인증코드 검증
  public MessageResponse verifyEmailCode(String email, String code){
    boolean isValid = mailService.codeVerification(email, code);

    if(!isValid){
      throw new IllegalArgumentException("이메일 인증 실패");
    }

    return MessageResponse.builder().message("이메일 인증 성공").build();

  }
}
