package com.board.repository;

import com.board.domain.Post;
import com.board.domain.PostCategory;
import com.board.domain.PostCategoryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostCategoryRepository extends JpaRepository<PostCategory, PostCategoryId> {
    @Modifying
    @Query("DELETE FROM PostCategory pc WHERE pc.post = :post")
    void deleteAllByPost(@Param("post") Post post);
}
