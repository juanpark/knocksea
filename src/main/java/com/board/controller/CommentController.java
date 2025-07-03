package com.board.controller;

import com.board.dto.CommentCreateRequest;
import com.board.dto.CommentUpdateRequest;
import com.board.dto.CommentResponse;
import com.board.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // 댓글 작성
    @PostMapping
    public ResponseEntity<CommentResponse> createComment(@RequestBody CommentCreateRequest request) {
        CommentResponse response = commentService.createComment(request);

        URI location = URI.create("/comments/" + response.getCommentId());
        return ResponseEntity.created(location) // 201 Created + Location 헤더
                                                .body(response); // 응답 본문 : CommentResponse json 객체
    }

    // 댓글 수정
    @PutMapping("/{commentId}")
    public ResponseEntity<Void> updateComment(
            @PathVariable Long commentId,
            @RequestBody CommentUpdateRequest request
    ) {
        commentService.updateComment(commentId, request);
        return ResponseEntity.noContent().build();
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

    // 댓글 채택
    @PostMapping("/{commentId}/adopt")
    public ResponseEntity<Void> adoptComment(@PathVariable Long commentId) {
        commentService.adoptComment(commentId);
        return ResponseEntity.ok().build();
    }

    // 특정 게시글의 최상위 댓글 목록 조회
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentResponse>> getCommentsByPost(@PathVariable Long postId) {
        List<CommentResponse> responses = commentService.getTopLevelCommentsByPost(postId);
        return ResponseEntity.ok(responses);
    }

    // 특정 사용자가 작성한 댓글 목록 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CommentResponse>> getCommentsByUser(@PathVariable Long userId) {
        List<CommentResponse> responses = commentService.getCommentsByUser(userId);
        return ResponseEntity.ok(responses);
    }
}