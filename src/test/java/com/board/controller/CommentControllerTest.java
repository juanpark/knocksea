package com.board.controller;

import com.board.dto.CommentCreateRequest;
import com.board.dto.CommentResponse;
import com.board.dto.CommentUpdateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Long testPostId;
    private Long testCommentId;

    // 댓글 생성 유틸 메서드
    private Long createTestComment(Long postId, Long memberId, String content) throws Exception {
        CommentCreateRequest request = new CommentCreateRequest();
        request.setPostId(postId);
        request.setUserId(memberId);
        request.setContent(content);

        String response = mockMvc.perform(post("/api/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        CommentResponse commentResponse = objectMapper.readValue(response, CommentResponse.class);
        return commentResponse.getCommentId();
    }

    // 테스트 전: 공통 댓글 생성
    @BeforeEach
    void setup() throws Exception {
        testPostId = 3L; // 이미 존재하는 게시글 ID로 가정
        testCommentId = createTestComment(testPostId, 3L, "테스트용 댓글");
    }

    // 테스트 후: 댓글 정리
    @AfterEach
    void cleanup() throws Exception {
        mockMvc.perform(delete("/api/comments/{id}", testCommentId))
                .andExpect(status().isOk());
    }

    // 댓글 등록 테스트
    @Test
    void commentInsertSuccess() throws Exception {
        CommentCreateRequest request = new CommentCreateRequest();
        request.setPostId(testPostId);
        request.setUserId(3L);
        request.setContent("유용한 글입니다!");

        mockMvc.perform(post("/api/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    // 댓글 조회 테스트
    @Test
    void getTopLevelCommentsByPost() throws Exception {
        mockMvc.perform(get("/api/comments/post/{postId}", testPostId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].commentId").exists())
                .andExpect(jsonPath("$[0].content").isNotEmpty());
    }

    // 댓글 없는 게시글 조회
    @Test
    void getTopLevelCommentsByPost_NoComments() throws Exception {
        Long emptyPostId = 999L; // 댓글 없는 게시글 ID라고 가정

        mockMvc.perform(get("/api/comments/post/{postId}", emptyPostId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // 댓글 수정 테스트
    @Test
    void commentUpdateSuccess() throws Exception {
        CommentUpdateRequest updateRequest = new CommentUpdateRequest();
        updateRequest.setContent("수정된 댓글입니다.");

        mockMvc.perform(patch("/api/comments/{id}", testCommentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());
    }

    // 댓글 삭제 테스트
    @Test
    void commentDeleteSuccess() throws Exception {
        Long deleteId = createTestComment(testPostId, 3L, "삭제용 댓글");

        mockMvc.perform(delete("/api/comments/{id}", deleteId))
                .andExpect(status().isOk());
    }
}