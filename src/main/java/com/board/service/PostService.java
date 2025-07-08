package com.board.service;

import com.board.domain.*;
import com.board.dto.PostRequestDto;
import com.board.dto.PostResponseDto;
import com.board.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

//쓰기/수정/삭제 -> @Transactional (실제 DB의 데이터를 실제로 바꿈. 즉, 에러나면 롤백 필요)
//읽기 -> @Transactional(readOnly = true) (조회는 데이터를 안바꿈. 롤백 기능 필요 없음)
//JPA가 트랜잭션 없으면 DB 커넥션을 관리 못함. 그래서 조회도 트랜잭션이 필요함

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepo;
    private final TagRepository tagRepo;
    private final MemberRepository memberRepository;
    private final PostTagRepository postTagRepository;
    private final PostCategoryRepository postCategoryRepository;
    private final CommentRepository commentRepository;

    //게시글 저장
    @Transactional
    public Long createPost(PostRequestDto requestDto, Long currentUserId) {
        validateRequest(requestDto);

        Post post = new Post();
        post.setTitle(requestDto.getTitle());
        post.setContent(requestDto.getContent());

        // 유저 검증
        Member member = memberRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        post.setMember(member); // 작성자 저장

        postRepository.save(post);

        // 중간 테이블: PostCategory
        if (requestDto.getCategoryId() != null) {
            Category category = categoryRepo.findById(requestDto.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));

            PostCategory postCategory = new PostCategory();
            postCategory.setPost(post);
            postCategory.setCategory(category);

            post.getPostCategories().add(postCategory);
            category.getPostCategories().add(postCategory);
        }

        // 중간 테이블: PostTag
        if (requestDto.getTagNames() != null && !requestDto.getTagNames().isEmpty()) {
            for (String tagName : requestDto.getTagNames()) {
                Tag tag = tagRepo.findByName(tagName)
                        .orElseGet(() -> tagRepo.save(new Tag(tagName)));

                PostTagId postTagId = new PostTagId();
                postTagId.setPostsId(post.getPostsId());
                postTagId.setTagId(tag.getId());

                PostTag postTag = new PostTag();
                postTag.setId(postTagId);
                postTag.setPost(post);
                postTag.setTag(tag);

                post.getPostTags().add(postTag);
                tag.getPostTags().add(postTag);
            }
        }

        return post.getPostsId();
    }

    //조회수 증가 (getPost에서 로직 분리)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void  increaseViewCount(Long id) {
        postRepository.increaseViewCount(id);
    }

    //게시글 조회
    @Transactional(readOnly = true)
    public PostResponseDto getPost(Long id) {
        //글이 삭제된 상태에서 조회 요청 보낼 수 있고, URL에서 직접 요청하는 경우도 있으므로 예외처리
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다."));

        // 댓글 수 조회
//        int commentCount = commentRepository.countByPostPostsId(id);

//        return convertToResponseDto(post, commentCount);
        return convertToResponseDto(post);
    }

    //게시글 전체 조회
    @Transactional(readOnly = true)
    public List<PostResponseDto> getAllPosts() {
        List<Post> posts = postRepository.findAll();
        List<PostResponseDto> responseList = new ArrayList<>();

        for (Post post : posts) {
            PostResponseDto dto = convertToResponseDto(post);
            responseList.add(dto);
        }

        return responseList;
    }

    //게시글 수정
    @Transactional
    public PostResponseDto updatePost(Long id, PostRequestDto requestDto, Long currentUserId) {
        validateRequest(requestDto);

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다!"));

        //유저 검증
        if (!post.getMember().getId().equals(currentUserId)) {
            throw new RuntimeException("작성자만 수정할 수 있습니다.");
        }

        post.setTitle(requestDto.getTitle());
        post.setContent(requestDto.getContent());
        
        //===== 카테고리 수정 =====
        postCategoryRepository.deleteAllByPost(post);
        post.getPostCategories().clear(); // 메모리에서도 삭제

        if (requestDto.getCategoryId() != null) {
            Category category = categoryRepo.findById(requestDto.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));

            PostCategory postCategory = new PostCategory();
            postCategory.setPost(post);
            postCategory.setCategory(category);

            post.getPostCategories().add(postCategory);
            category.getPostCategories().add(postCategory);
        }

        //===== 태그 수정 =====
        //기존 태그 DB에서 삭제
        postTagRepository.deleteAllByPost(post);
        post.getPostTags().clear();

        if (requestDto.getTagNames() != null && !requestDto.getTagNames().isEmpty()) {
            for (String tagName : requestDto.getTagNames()) {
                Tag tag = tagRepo.findByName(tagName)
                        .orElseGet(() -> tagRepo.save(new Tag(tagName)));

                PostTagId postTagId = new PostTagId();
                postTagId.setPostsId(post.getPostsId());
                postTagId.setTagId(tag.getId());

                PostTag postTag = new PostTag();
                postTag.setId(postTagId);
                postTag.setPost(post);
                postTag.setTag(tag);

                post.getPostTags().add(postTag);
                tag.getPostTags().add(postTag);
            }
        }
        return convertToResponseDto(post);
    }

    //게시글 삭제
    @Transactional
    public void deletePost(Long id, Long currentUserId) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다!"));

        //유저 검증
        if (!post.getMember().getId().equals(currentUserId)) {
            throw new RuntimeException("작성자만 삭제할 수 있습니다.");
        }

        //해당 id에 대한 글에 대한 정보 모든 것을 삭제
        postRepository.delete(post);
    }

    //게시글 검증
    private void validateRequest(PostRequestDto requestDto) {
        //제목에 값이 없는 경우 (null, " ", "")를 걸러냄
        if (requestDto.getTitle() == null || requestDto.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("제목은 필수입니다.");
        }
        if (requestDto.getContent() == null || requestDto.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("내용은 필수입니다.");
        }
    }

    //Entity에서 DTO로 변환해주는 메서드
    private PostResponseDto convertToResponseDto(Post post) {
        PostResponseDto dto = new PostResponseDto();
        dto.setPostsId(post.getPostsId());
        dto.setUserId(post.getMember().getId()); // 댓글 사용자 인증을 위해 추가
        dto.setUserName(post.getMember().getNickname());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setLikeCount(post.getLikeCount());
        dto.setDislikeCount(post.getDislikeCount());
        dto.setStatus(post.getStatus().name());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setUpdatedAt(post.getUpdatedAt());
        dto.setViewCount(post.getViewCount());
        dto.setCommentCount(post.getComments().size());

        // 카테고리/태그 이름 목록 추출
        dto.setCategoryNames(post.getPostCategories().stream()
            .map(pc -> pc.getCategory().getName())
            .collect(Collectors.toList()));

        dto.setTagNames(post.getPostTags().stream()
            .map(pt -> pt.getTag().getName())
            .collect(Collectors.toList()));

        return dto;
    }

    //페이징 처리
    @Transactional(readOnly = true)
    public Page<PostResponseDto> getPostsByPage(int page, int size, String sort) {
        //몇 번째 페이지, 몇 개씩 가져올지, 글을 어떤 순서로 정렬할지 결정
        Pageable pageable = createPageable(page, size, sort);
        Page<Post> postPage = postRepository.findAll(pageable);

        //Post 엔티티 -> DTO
        return postPage.map(this::convertToResponseDto);
    }

    //게시글 검색 기능
    @Transactional(readOnly = true)
    public Page<PostResponseDto> searchPostsByKeyword(String keyword, int page, int pageSize, String sort) {
        Pageable pageable = createPageable(page, pageSize, sort);

        //제목 또는 내용에 keyword가 포함된 게시글 조회
        Page<Post> postPage = postRepository.findByTitleContainingOrContentContaining(keyword, keyword, pageable);

        return postPage.map(post -> {
            PostResponseDto dto = convertToResponseDto(post);

            //하이라이팅 처리, ?!로 대소문자 무시하여 검색어 발견하면 감쌈
            if (keyword != null && !keyword.isEmpty()) {
                String regex = "(?i)(" + Pattern.quote(keyword) + ")";
                String highlightedTitle = post.getTitle().replaceAll(regex, "<span class='highlight'>$1</span>");
                dto.setTitle(highlightedTitle);
            }

            return dto;
        });
    }
    
    // 카테고리 검색 기능
    @Transactional(readOnly = true)
    public Page<PostResponseDto> getPostsByCategory(String categoryName, int page, int pageSize, String sort) {
    	Pageable pageable = createPageable(page, pageSize, sort);
    	Page<Post> postPage = postRepository.findByCategoryName(categoryName, pageable);
        return postPage.map(this::convertToResponseDto);
    }

    //정렬 기능
    private Pageable createPageable(int page, int size, String sort) {
        Sort sortOption;
        switch (sort) {
            //조회수로 정렬
            case "views":
                sortOption = Sort.by(Sort.Direction.DESC, "viewCount");
                break;
                //최신순으로 정렬
            case "recent":
            default:
                sortOption = Sort.by(Sort.Direction.DESC, "postsId");
                break;
        }

        return PageRequest.of(page, size, sortOption);
    }

    @Transactional
    public void updatePostStatusByComments(Post post) {
        System.out.println("📌 post 상태 갱신 시도 - postId: " + post.getPostsId());

        int commentCount = post.getComments().size();

        List<Comment> remainingComments = commentRepository.findByPost(post);
        System.out.println("📌 남은 댓글 수: " + remainingComments.size());

        if (commentCount == 0) {
            post.setStatus(Post.Status.WAITING);
        }

        if (remainingComments.stream().noneMatch(Comment::isAnswer)) {
            post.setStatus(Post.Status.COMPLETED);
            System.out.println("📌 post 상태 → COMPLETED 로 변경됨");
        }
    }


}