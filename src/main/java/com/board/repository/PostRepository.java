package com.board.repository;

import com.board.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {

    //조회수 증가 쿼리
    @Modifying(clearAutomatically = true) //데이터 변경
    @Query("UPDATE Post p SET p.viewCount = p.viewCount + 1 WHERE p.postsId = :id")
    void increaseViewCount(@Param("id") Long id);

}