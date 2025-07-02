package com.board.dto;

import lombok.Builder;
import lombok.Getter;


/*테스트입니다*/
@Getter
@Builder
public class MemberResponse {
  private String email;
  private String nickname;
  private String name;

}
