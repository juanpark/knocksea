package com.board.controller;

import com.board.dto.CommentCreateRequest;
import com.board.dto.CommentResponse;
import com.board.dto.CommentUpdateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    private Long testCommentId;

    // 테스트용 댓글 생성 유틸 메서드
    // 댓글을 생성하고 해당 댓글의 ID를 반환
    private Long createTestComment() throws Exception {
        CommentCreateRequest request = new CommentCreateRequest();
        request.setPostId(3L);
        request.setUserId(3L);
        request.setContent("공통 댓글 생성");

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

    @BeforeEach
    void setup() throws Exception {
        testCommentId = createTestComment();
    }

    // 댓글 등록 테스트
    @Test
    void commentInsertSuccess() throws Exception {
        CommentCreateRequest request = new CommentCreateRequest();
        request.setPostId(1L); // 미리 DB에 게시글 등록되어있어야 함
        request.setUserId(3L);
        request.setContent("진짜 유용한 글! 인정입니닷");

        // when & then
        mockMvc.perform(post("/api/comments")
                .contentType(MediaType.APPLICATION_JSON) // 요청 본문의 타입 지정
                .content(objectMapper.writeValueAsString(request))) // ObjectMapper는 JSON 직렬화/역직렬화 도구
                .andExpect(status().isCreated()); // 201  반환
    }
    
    // 댓글 조회 테스트
    @Test
    void getTopLevelCommentsByPost() throws Exception {
        // given (게시글 ID에 댓글이 존재하는 경우)
        Long postId = 1L; // 사전 등록된 게시글 ID

        // when & then
        mockMvc.perform(get("/api/comments/post/{postId}/top", postId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray()) // 결과가 배열인지
                .andExpect(jsonPath("$[0].commentId").exists()) // 첫 댓글에 ID 있는지
                .andExpect(jsonPath("$[0].content").isNotEmpty()); // 내용이 비어있지 않은지
    }


    // 댓글 수정 테스트
    @Test
    void commentUpdateSuccess() throws Exception {
        CommentUpdateRequest request = new CommentUpdateRequest();
        request.setContent("수정된 댓글 내용입니다.");

        mockMvc.perform(patch("/api/comments/{id}", testCommentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    // 댓글 삭제 테스트
    @Test
    void commentDeleteSuccess() throws Exception {
        mockMvc.perform(delete("/api/comments/{id}", testCommentId))
                .andExpect(status().isOk());
    }
}
