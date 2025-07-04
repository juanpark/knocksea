package com.board.service;

import com.board.auth.CustomUserDetails;
import com.board.domain.Comment;
import com.board.domain.Post;
import com.board.domain.Member;
import com.board.dto.CommentCreateRequest;
import com.board.dto.CommentUpdateRequest;
import com.board.dto.CommentResponse;
import com.board.exception.CommentNotFoundException;
import com.board.exception.CustomException;
import com.board.exception.UnauthorizedCommentAccessException;
import com.board.exception.UnauthorizedCommentAdoptException;
import com.board.repository.CommentRepository;
import com.board.repository.PostRepository;
import com.board.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    // 댓글 작성
    public CommentResponse createComment(CommentCreateRequest request) {
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));
        Member member = memberRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));

        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setMember(member);
        comment.setPost(post);

        // 대댓글일 경우
        if (request.getParentId() != null) {
            Comment parent = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("부모 댓글이 존재하지 않습니다."));
            parent.addChildComment(comment);
        }

        Comment saved = commentRepository.save(comment);
        return toResponse(saved);
    }

    // 댓글 수정
    /*public void updateComment(Long commentId, CommentUpdateRequest request, Long currentUserId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));

        // 로그인한 사용자와 댓글 작성자가 일치하는지 확인
        if (!comment.getMember().getId().equals(currentUserId)) {
            throw new SecurityException("해당 댓글을 수정할 권한이 없습니다.");
        }

        comment.setContent(request.getContent());
        comment.setUpdatedAt(java.time.LocalDateTime.now());
    }*/
    public void updateComment(Long commentId, CommentUpdateRequest request, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);

        if (!comment.getMember().getId().equals(userId)) {
            throw new UnauthorizedCommentAccessException(); // 본인만 수정 가능
        }

        comment.setContent(request.getContent());
        comment.setUpdatedAt(LocalDateTime.now());
    }

    // 댓글 삭제
    /*public void deleteComment(Long commentId, Long currentUserId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));

        // 로그인한 사용자와 댓글 작성자가 일치하는지 확인
        if (!comment.getMember().getId().equals(currentUserId)) {
            throw new SecurityException("해당 댓글을 삭제할 권한이 없습니다.");
        }

        commentRepository.delete(comment);
    }*/
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);

        if (!comment.getMember().getId().equals(userId)) {
            throw new UnauthorizedCommentAccessException(); // 본인만 삭제 가능
        }

        commentRepository.delete(comment);
    }

    // 댓글 채택
    public void adoptComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);

        Post post = comment.getPost();

        // 게시글 작성자와 현재 로그인 사용자가 일치하는지 확인
        if (!post.getMember().getId().equals(userId)) {
            throw new UnauthorizedCommentAdoptException(); // 채택 권한 없음
        }

        comment.setAnswer(true); // 채택된 댓글 표시
        post.setStatus(Post.Status.ADOPTED); // 게시글 상태 변경
    }


    // 게시글 ID로 최상위 댓글 목록 조회
    public List<CommentResponse> getTopLevelCommentsByPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        List<Comment> comments = commentRepository.findByPostAndParentIsNull(post);
        return comments.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // 특정 사용자가 작성한 모든 댓글 조회
    public List<CommentResponse> getCommentsByUser(Long userId) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));
        List<Comment> comments = commentRepository.findByMember(member);
        return comments.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Entity → DTO 변환 (수동 Mapper 방법 사용)
    private CommentResponse toResponse(Comment comment) {
        CommentResponse dto = new CommentResponse();
        dto.setCommentId(comment.getCommentId());
        dto.setUserId(comment.getMember().getId());
        dto.setNickname(comment.getMember().getNickname());
        dto.setContent(comment.getContent());
        dto.setAnswer(comment.isAnswer());
        dto.setLikeCount(comment.getLikeCount());
        dto.setDislikeCount(comment.getDislikeCount());
        dto.setCreateAt(comment.getCreateAt());
        dto.setUpdatedAt(comment.getUpdatedAt());
        dto.setParentId(comment.getParent() != null ? comment.getParent().getCommentId() : null);

        // 재귀적으로 대댓글 추가
        List<CommentResponse> children = comment.getChildren().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        dto.setChildren(children);

        return dto;
    }
}