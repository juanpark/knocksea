package com.board.controller;

import com.board.dto.VoteRequestDto;
import com.board.entity.*;
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
        // 임시 유저 생성 (나중에 로그인 연동되면 교체)
        User mockUser = User.builder().id(1L).nickname("guest").build();

        voteService.vote(mockUser, dto.getTargetId(), dto.getTargetType(), dto.getIsLike());

        return ResponseEntity.ok("투표 완료!");
    }

    @GetMapping("/count")
    public ResponseEntity<?> countVotes(@RequestParam Long targetId,
                                        @RequestParam TargetType targetType,
                                        @RequestParam VoteType isLike) {
        long count = voteService.countVotes(targetId, targetType, isLike);
        return ResponseEntity.ok(count);
    }
}
