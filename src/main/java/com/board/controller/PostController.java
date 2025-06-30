package com.board.controller;

import com.board.dto.PostRequestDto;
import com.board.dto.PostResponseDto;
import com.board.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    //게시글 리스트 페이지
    @GetMapping
    public String getPostList(Model model) {
        List<PostResponseDto> posts = postService.getAllPosts();
        model.addAttribute("posts", posts);
        return "post-list";
    }

    //게시글 작성 페이지
    @GetMapping("/create")
    public String createPostForm() {
        return "post-create";
    }

    //게시글 작성 처리
    @PostMapping("/create")
    //form에서 받은 내용을 받아 service.createPost 메서드 실행
    public String createPost(@ModelAttribute PostRequestDto requestDto) {
        postService.createPost(requestDto);
        return "redirect:/posts";
    }

    //게시글 상세 페이지
    @GetMapping("/{id}")
    public String getPost(@PathVariable Long id, Model model) {
        //해당 id를 조회 후 dto로 가공
        PostResponseDto post = postService.getPost(id);
        model.addAttribute("post", post);
        return "post-detail";
    }

    //게시글 수정 페이지(GET)
    @GetMapping("/{id}/edit")
    public String editPostForm(@PathVariable Long id, Model model) {
        PostResponseDto post = postService.getPost(id);
        model.addAttribute("post", post);
        return "post-edit";
    }

    //게시글 수정 페이지(POST)
    @PostMapping("/{id}/edit")
    public String updatePost(@PathVariable Long id, @ModelAttribute PostRequestDto requestDto) {
        postService.updatePost(id, requestDto);
        return "redirect:/posts/" + id; //수정 후 상세 페이지로 리다이렉트
    }

    //게시글 삭제 처리
    @PostMapping("/{id}/delete")
    public String deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return "redirect:/posts";
    }
}