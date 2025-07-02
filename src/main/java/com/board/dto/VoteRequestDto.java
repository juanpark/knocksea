package com.board.dto;

import com.board.entity.TargetType;
import com.board.entity.VoteType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VoteRequestDto {
    private Long targetId;
    private TargetType targetType;
    private VoteType voteType;
}

