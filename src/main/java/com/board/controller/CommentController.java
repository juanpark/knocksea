package com.board.controller;

import com.board.auth.CustomUserDetails;
import com.board.dto.CommentCreateRequest;
import com.board.dto.CommentUpdateRequest;
import com.board.dto.CommentResponse;
import com.board.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // 현재 로그인한 사용자 ID 가져오기 (JWT 또는 OAuth2 인증 기반)
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            throw new RuntimeException("로그인이 필요합니다.");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUserDetails customUserDetails) {
            // 자바 16 이상에서 도입된 패턴 매칭 for instanceof 기능
            // 조건을 만족하면 자동으로 customUserDetails라는 변수에 캐스팅된 결과를 넣어줌.
            return customUserDetails.getMember().getId(); // JWT 로그인 사용자
        }

        // OAuth2 로그인 사용자 처리 예시 (ID 추출 방식은 실제 구현에 따라 조정 필요)
        if (principal instanceof org.springframework.security.oauth2.core.user.OAuth2User oauth2User) {
            Object id = oauth2User.getAttribute("id");
            if (id != null) return Long.parseLong(id.toString());
        }

        throw new RuntimeException("사용자 인증 정보를 찾을 수 없습니다.");
    }

    // 댓글 작성 (로그인 사용자만 가능)
    @PostMapping
    public ResponseEntity<CommentResponse> createComment(@RequestBody CommentCreateRequest request) {
        Long userId = getCurrentUserId();
        request.setUserId(userId);        // 직접 전달받지 않고 주입

        CommentResponse response = commentService.createComment(request);
        URI location = URI.create("/api/comments/" + response.getCommentId());
        return ResponseEntity.created(location).body(response); // 201 Created
    }

    // 댓글 수정 (로그인 사용자만 가능)
    @PutMapping("/{commentId}")
    public ResponseEntity<Void> updateComment(
            @PathVariable Long commentId,
            @RequestBody CommentUpdateRequest request
    ) {
        Long userId = getCurrentUserId();
        commentService.updateComment(commentId, request, userId); // 내부 로직에서 작성자 확인 로직 권장
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    // 댓글 삭제 (로그인 사용자만 가능)
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        Long userId = getCurrentUserId();
        commentService.deleteComment(commentId, userId); // 현재 로그인한 사용자만 삭제 가능
        return ResponseEntity.noContent().build();
    }

    // 댓글 채택 (로그인 사용자만 가능, 글 작성자여야 함)
    @PostMapping("/{commentId}/adopt")
    public ResponseEntity<Void> adoptComment(@PathVariable Long commentId) {
        Long userId = getCurrentUserId(); // 로그인 사용자 ID
        commentService.adoptComment(commentId, userId); // 권한 검증 포함
        return ResponseEntity.ok().build();
    }

    // 특정 게시글의 최상위 댓글 목록 조회 (비로그인도 가능)
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentResponse>> getCommentsByPost(@PathVariable Long postId) {
        List<CommentResponse> responses = commentService.getTopLevelCommentsByPost(postId);
        return ResponseEntity.ok(responses);
    }

    // 특정 사용자가 작성한 댓글 목록 조회 (로그인 사용자와 일치해야 하거나 관리자 권한 필요)
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CommentResponse>> getCommentsByUser(@PathVariable Long userId) {
        Long currentUserId = getCurrentUserId();
        if (!userId.equals(currentUserId)) {
            throw new RuntimeException("본인의 댓글만 조회할 수 있습니다.");
        }

        List<CommentResponse> responses = commentService.getCommentsByUser(userId);
        return ResponseEntity.ok(responses);
    }
}
