package com.board.repository;

import com.board.domain.Post;
import com.board.domain.PostTag;
import com.board.domain.PostTagId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostTagRepository extends JpaRepository<PostTag, PostTagId> {
    @Modifying
    @Query("DELETE FROM PostTag pt WHERE pt.post = :post")
    void deleteAllByPost(@Param("post") Post post);
}
