package com.board.repository;

import com.board.domain.Member;
import com.board.entity.Vote;
import com.board.entity.TargetType;
import com.board.entity.VoteType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    // 중복 투표 방지용 (한 멤버가 같은 대상에 두 번 투표 못 하게)
    Optional<Vote> findByMemberAndTargetIdAndTargetType(Member member, Long targetId, TargetType targetType);

    // 추천 수 / 비추천 수 세기
    long countByTargetIdAndTargetTypeAndVoteType(Long targetId, TargetType targetType, VoteType voteType);
}
