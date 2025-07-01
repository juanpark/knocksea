package com.board.repository;

import com.board.domain.Token;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token, Long> {

  void deleteByMemberId(Long memberId);

  Optional<Token> findByMemberId(Long memberId);

}
