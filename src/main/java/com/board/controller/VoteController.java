package com.board.controller;

import com.board.auth.CustomUserDetails;
import com.board.domain.Member;
import com.board.dto.VoteRequestDto;
import com.board.entity.TargetType;
import com.board.entity.VoteType;
import com.board.service.VoteService;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/votes")
@Slf4j
public class VoteController {

    private final VoteService voteService;

    @PostMapping
    public ResponseEntity<?> vote(@RequestBody VoteRequestDto dto) {
        Member member = getCurrentUser(); //현재 로그인 유저 ID 추출

        voteService.vote(member, dto.getTargetId(), dto.getTargetType(), dto.getVoteType());
        log.info("투표 성공");
        return ResponseEntity.ok("투표 완료!");
    }

    @GetMapping("/count")
    public ResponseEntity<?> countVotes(@RequestParam Long targetId,
        @RequestParam TargetType targetType) {
        long likeCount = voteService.countVotes(targetId, targetType, VoteType.LIKE);
        long dislikeCount = voteService.countVotes(targetId, targetType, VoteType.DISLIKE);
        return ResponseEntity.ok(Map.of("likeCount", likeCount, "dislikeCount", dislikeCount));
    }

    /**
     * 투표 취소
     */
    @DeleteMapping
    public ResponseEntity<?> cancelVote(@RequestParam Long targetId,
        @RequestParam TargetType targetType) {
        Member member = getCurrentUser(); //현재 로그인 유저 추출

        voteService.cancelVote(member, targetId, targetType);

        return ResponseEntity.ok("투표 취소 완료!");
    }

    // 유저 ID 추출
    private Member getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            throw new RuntimeException("로그인이 필요합니다.");
        }

        Object principal = authentication.getPrincipal();

        //JWT: CustomUserDetails를 저장한 경우
        if (principal instanceof UserDetails userDetails) {
            CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
            return customUserDetails.getMember();
        }

        // OAuth: OAuth2User 타입인 경우
//        if (principal instanceof OAuth2User oauth2User) {
//            Object id = oauth2User.getAttribute("id");
//            if (id != null) {
//                return Long.parseLong(id.toString());
//            } else {
//                throw new RuntimeException("OAuth 유저 ID를 찾을 수 없습니다.");
//            }
//        }

        throw new RuntimeException("로그인이 필요합니다.");
    }

    /**
     * 현재 로그인 사용자의 해당 대상에 대한 투표 타입 조회
     */
    @GetMapping("/user")
    public ResponseEntity<?> getUserVote(@RequestParam Long targetId,
        @RequestParam TargetType targetType) {
        Member member = getCurrentUser();

        VoteType voteType = voteService.getUserVoteType(member, targetId, targetType);

        Map<String, Object> res = new HashMap<>();
        res.put("voteType", voteType != null ? voteType : null);

        return ResponseEntity.ok(res);
    }

}