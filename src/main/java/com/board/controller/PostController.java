package com.board.controller;

import com.board.auth.CustomUserDetails;
import com.board.domain.Category;
import com.board.domain.Post;
import com.board.dto.PostRequestDto;
import com.board.dto.PostResponseDto;
import com.board.service.CategoryService;
import com.board.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final CategoryService categoryService;

    //게시글 리스트 페이지 -> 페이징 처리하면서 주석 처리
//    @GetMapping
//    public String getPostList(Model model) {
//        List<PostResponseDto> posts = postService.getAllPosts();
//        model.addAttribute("posts", posts);
//        return "post-list";
//    }

    //게시글 작성 페이지
    @GetMapping("/create")
    public String createPostForm(Model model) {
    	List<Category> categories = categoryService.findAll();
        model.addAttribute("categories", categories);
        return "post-create";
    }

    //게시글 작성 처리
    @PostMapping("/create")
    //form에서 받은 내용을 받아 service.createPost 메서드 실행, 현재 로그인 유저 추출
    public String createPost(@RequestBody PostRequestDto requestDto)
    {

        Long userId = getCurrentUserId();
        postService.createPost(requestDto, userId);
        return "redirect:/posts/page";
    }

    //조회수 증가
    @PostMapping("/{id}/viewCount")
    @ResponseBody
    public ResponseEntity<Void> increaseViewCount(@PathVariable Long id) {
        postService.increaseViewCount(id);
        return ResponseEntity.ok().build();
    }

    //게시글 상세 페이지
    @GetMapping("/{id}")
    public String getPost(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        //해당 id를 조회 후 dto로 가공
        PostResponseDto post = postService.getPost(id);
        model.addAttribute("post", post);

        // 로그인 사용자 id 추가 : 댓글 기능의 현재 로그인된 사용자 정보 필요
//        Long currentUserId = (userDetails != null) ? userDetails.getMember().getId() : null;
        Long currentUserId = 6L; // 디버깅용! 실제 배포 전에 제거해야 함!!!!
        model.addAttribute("currentUserId", currentUserId);
        // 확인
//        System.out.println("확인 userDetails = " + userDetails);
//        System.out.println("확인 currentUserId = " + currentUserId);

        return "post-detail";
    }

    //게시글 수정 페이지(GET)
    @GetMapping("/{id}/edit")
    public String editPostForm(@PathVariable Long id, Model model) {
        PostResponseDto post = postService.getPost(id);
        List<Category> categories = categoryService.findAll();
        model.addAttribute("post", post);
        model.addAttribute("categories", categories);
        return "post-edit";
    }

    //게시글 수정 페이지(POST)
    @PostMapping("/{id}/edit")
    public String updatePost(@PathVariable Long id,
                             @RequestBody PostRequestDto requestDto)

    {
        Long userId = getCurrentUserId();
        postService.updatePost(id, requestDto, userId);
        return "redirect:/posts/" + id + "?from=edit"; //수정 후 상세 페이지로 리다이렉트
    }

    //게시글 삭제 처리
    @PostMapping("/{id}/delete")
    @ResponseBody
    public ResponseEntity<String> deletePost(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        postService.deletePost(id, userId);
        return ResponseEntity.ok("삭제 성공");
    }

    //페이징 조회 → URL 변경, 검색어 기능 추가, 정렬 기능 추가, 카테고리 기능 추가
    @GetMapping("/page")
    public String getPostList(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(required = false) String keyword,
                              @RequestParam(required = false) String categoryName,
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
        } else if(categoryName != null && !categoryName.isEmpty()) {
        	posts = postService.getPostsByCategory(categoryName, page, pageSize, sort);
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
        model.addAttribute("categoryName", categoryName);

        return "post-list";
    }

    //유저 ID 추출
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            throw new RuntimeException("로그인이 필요합니다.");
        }

        Object principal = authentication.getPrincipal();

        //JWT: CustomUserDetails를 저장한 경우
        if (principal instanceof UserDetails userDetails) {
            CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
            return customUserDetails.getMember().getId();
        }

        //OAuth: OAuth2User 타입인 경우
        if (principal instanceof OAuth2User oauth2User) {
            Object id = oauth2User.getAttribute("id");
            if (id != null) {
                return Long.parseLong(id.toString());
            } else {
                throw new RuntimeException("OAuth 유저 ID를 찾을 수 없습니다.");
            }
        }

        throw new RuntimeException("로그인이 필요합니다.");
    }

    //map 관련 메서드
    @GetMapping("/api")
    @ResponseBody
    public List<PostResponseDto> getAllPostsApi() {
        return postService.getAllPosts();
    }
}
