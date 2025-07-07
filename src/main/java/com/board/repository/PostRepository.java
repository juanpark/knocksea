package com.board.repository;

import com.board.domain.Post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {

    //조회수 증가 쿼리
    @Modifying(clearAutomatically = false) //데이터 변경
    @Query(value = "UPDATE posts SET view_count = view_count + 1 WHERE posts_id = :id", nativeQuery = true)
    void increaseViewCount(@Param("id") Long id);

    //제목 또는 내용에 keyword가 포함된 게시글 페이지 조회
    Page<Post> findByTitleContainingOrContentContaining(String title, String content, Pageable pageable);

    // 카테고리로 게시글 조회
    @Query("SELECT p FROM Post p " +
            "JOIN p.postCategories pc " +
            "JOIN pc.category c " +
            "WHERE c.name = :categoryName")
    Page<Post> findByCategoryName(@Param("categoryName") String categoryName, Pageable pageable);

}
