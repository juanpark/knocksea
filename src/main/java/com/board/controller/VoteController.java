package com.board.controller;

import com.board.domain.Member;
import com.board.dto.VoteRequestDto;
import com.board.entity.TargetType;
import com.board.entity.VoteType;
import com.board.service.VoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/votes")
public class VoteController {

    private final VoteService voteService;

    @PostMapping
    public ResponseEntity<?> vote(@RequestBody VoteRequestDto dto) {
        // 임시 Member 생성 (나중에 로그인 연동되면 교체)
        Member mockMember = Member.builder().id(1L).nickname("guest").build();

        voteService.vote(mockMember, dto.getTargetId(), dto.getTargetType(), dto.getVoteType());

        return ResponseEntity.ok("투표 완료!");
    }

    @GetMapping("/count")
    public ResponseEntity<?> countVotes(@RequestParam Long targetId,
                                        @RequestParam TargetType targetType,
                                        @RequestParam VoteType voteType) {
        long count = voteService.countVotes(targetId, targetType, voteType);
        return ResponseEntity.ok(count);
    }
}
