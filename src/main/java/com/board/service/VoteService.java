package com.board.service;

import com.board.domain.Member;
import com.board.entity.TargetType;
import com.board.entity.Vote;
import com.board.entity.VoteType;
import com.board.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;

    /**
     * 투표 요청 처리 (추천 or 비추천)
     */
    @Transactional
    public void vote(Member member, Long targetId, TargetType targetType, VoteType voteType) {
        voteRepository.findByMemberAndTargetIdAndTargetType(member, targetId, targetType)
                .ifPresentOrElse(existingVote -> {
                    if (existingVote.getVoteType() == voteType) {
                        throw new IllegalArgumentException("이미 동일한 투표를 했습니다.");
                    }
                    // 다른 투표를 했으면 수정
                    existingVote.setVoteType(voteType);
                }, () -> {
                    // 첫 투표 시 저장
                    Vote vote = Vote.builder()
                            .member(member)
                            .targetId(targetId)
                            .targetType(targetType)
                            .voteType(voteType)
                            .build();
                    voteRepository.save(vote);
                });
    }

    /**
     * 투표 취소
     */
    @Transactional
    public void cancelVote(Member member, Long targetId, TargetType targetType) {
        Vote vote = voteRepository.findByMemberAndTargetIdAndTargetType(member, targetId, targetType)
                .orElseThrow(() -> new IllegalArgumentException("투표 기록이 없습니다."));

        voteRepository.delete(vote);
    }

    /**
     * 추천/비추천 수 반환
     */
    public long countVotes(Long targetId, TargetType targetType, VoteType voteType) {
        return voteRepository.countByTargetIdAndTargetTypeAndVoteType(targetId, targetType, voteType);
    }
}