package com.board.controller;

import com.board.auth.CustomUserDetails;
import com.board.dto.VoteRequestDto;
import com.board.entity.TargetType;
import com.board.entity.VoteType;
import com.board.service.VoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/votes")
public class VoteController {

    private final VoteService voteService;

    @PostMapping
    public ResponseEntity<?> vote(@RequestBody VoteRequestDto dto) {
        Long userId = getCurrentUserId(); //현재 로그인 유저 ID 추출

        voteService.vote(userId, dto.getTargetId(), dto.getTargetType(), dto.getVoteType());

        return ResponseEntity.ok("투표 완료!");
    }

    @GetMapping("/count")
    public ResponseEntity<?> countVotes(@RequestParam Long targetId,
                                        @RequestParam TargetType targetType,
                                        @RequestParam VoteType voteType) {
        long count = voteService.countVotes(targetId, targetType, voteType);
        return ResponseEntity.ok(count);
    }

    /**
     * 투표 취소
     */
    @DeleteMapping
    public ResponseEntity<?> cancelVote(@RequestParam Long targetId,
                                        @RequestParam TargetType targetType) {
        Long userId = getCurrentUserId(); //현재 로그인 유저 ID 추출

        voteService.cancelVote(userId, targetId, targetType);

        return ResponseEntity.ok("투표 취소 완료!");
    }

    // 유저 ID 추출
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            throw new RuntimeException("로그인이 필요합니다.");
        }

        Object principal = authentication.getPrincipal();

        //JWT: CustomUserDetails를 저장한 경우
        if (principal instanceof UserDetails userDetails) {
            CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
            return customUserDetails.getMember().getId();
        }

        // OAuth: OAuth2User 타입인 경우
        if (principal instanceof OAuth2User oauth2User) {
            Object id = oauth2User.getAttribute("id");
            if (id != null) {
                return Long.parseLong(id.toString());
            } else {
                throw new RuntimeException("OAuth 유저 ID를 찾을 수 없습니다.");
            }
        }

        throw new RuntimeException("로그인이 필요합니다.");
    }
}