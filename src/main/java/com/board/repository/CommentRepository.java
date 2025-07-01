package com.board.repository;

import com.board.domain.Comment;
import com.board.domain.Post;
import com.board.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 특정 게시글에 달린 모든 댓글 조회
    List<Comment> findByPost(Post post);

    // 질문 상세 페이지에 들어가면, 해당 글에 달린 댓글들을 다 보여 줌
    // 특정 게시글 ID로 댓글 조회
    List<Comment> findByPost_PostsId(Long postId);

    // 마이페이지(활동 내역 보기)에서 내가 작성한 댓글 목록 보기/채택된 댓글 통계용으로도 활용 가능
    // 특정 사용자가 작성한 댓글 조회
    List<Comment> findByUser(Member member);

    // 대댓글(댓글의 댓글)이 있는 구조에서 계층적으로 표현
    // 부모 댓글이 없는 (최상위 댓글)만 조회
    List<Comment> findByPostAndParentIsNull(Post post);

    // 특정 댓글의 자식 대댓글 조회
    List<Comment> findByParent(Comment parent);
}