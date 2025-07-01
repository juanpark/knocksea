package com.board.service;

import com.board.entity.*;
import com.board.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;

    /**
     * 투표 요청 처리 (추천 or 비추천)
     */
    public void vote(User user, Long targetId, TargetType targetType, VoteType isLike) {
        // 이미 투표한 적 있는지 확인
        Optional<Vote> existingVote = voteRepository.findByUserAndTargetIdAndTargetType(user, targetId, targetType);

        if (existingVote.isPresent()) {
            throw new IllegalArgumentException("이미 투표한 대상입니다.");
        }

        // 투표 객체 저장
        Vote vote = Vote.builder()
                .user(user)
                .targetId(targetId)
                .targetType(targetType)
                .isLike(isLike)
                .build();

        voteRepository.save(vote);
    }

    /**
     * 추천/비추천 수 반환
     */
    public long countVotes(Long targetId, TargetType targetType, VoteType isLike) {
        return voteRepository.countByTargetIdAndTargetTypeAndIsLike(targetId, targetType, isLike);
    }
}
