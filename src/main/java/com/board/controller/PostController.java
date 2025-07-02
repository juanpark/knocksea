package com.board.controller;

import com.board.domain.Post;
import com.board.dto.PostRequestDto;
import com.board.dto.PostResponseDto;
import com.board.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    //게시글 리스트 페이지 -> 페이징 처리하면서 주석 처리
//    @GetMapping
//    public String getPostList(Model model) {
//        List<PostResponseDto> posts = postService.getAllPosts();
//        model.addAttribute("posts", posts);
//        return "post-list";
//    }

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
        return "redirect:/posts/page";
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
        return "redirect:/posts/page";
    }

    //페이징 조회 → URL 변경, 검색어 기능 추가, 정렬 기능 추가
    @GetMapping("/page")
    public String getPostList(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(required = false) String keyword,
                              @RequestParam(defaultValue = "recent") String sort,
                              Model model)
    {
        if (page < 0) {
            page = 0;
        }

        int pageSize = 10;
        Page<PostResponseDto> posts;

        //검색어 있으면 검색 결과 페이지 조회
        if (keyword != null && !keyword.isEmpty()) {
            posts = postService.searchPostsByKeyword(keyword, page, pageSize, sort);
        } else {
            posts = postService.getPostsByPage(page, pageSize, sort);
        }

        int totalPages = posts.getTotalPages();
        if (totalPages == 0) {
            totalPages = 1; //1페이지부터 보장
        }

        model.addAttribute("posts", posts.getContent()); //현재 페이지 글 목록
        model.addAttribute("currentPage", page + 1);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("keyword", keyword); //검색어
        model.addAttribute("sort", sort); //정렬

        return "post-list";
    }

    // 카테고리, 태그, 상태로 검색
    @GetMapping
    public ResponseEntity<List<Post>> getPosts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long tagId,
            @RequestParam(required = false) Post.Status status
    ) {
        return ResponseEntity.ok(postService.searchPosts(categoryId, tagId, status));
    }

    // 질문상태 업데이트
    @PatchMapping("/{postId}/status")
    public String updateStatus(
            @PathVariable Long postId,
            @RequestParam Post.Status status
    ) {
        postService.updateStatus(postId, status);
        return "redirect:/post-detail";
    }
}
