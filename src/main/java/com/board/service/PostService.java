package com.board.service;

import com.board.domain.Category;
import com.board.domain.Post;
import com.board.domain.Tag;
import com.board.dto.PostRequestDto;
import com.board.dto.PostResponseDto;
import com.board.repository.CategoryRepository;
import com.board.repository.PostRepository;
import com.board.repository.TagRepository;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

//쓰기/수정/삭제 -> @Transactional (실제 DB의 데이터를 실제로 바꿈. 즉, 에러나면 롤백 필요)
//읽기 -> @Transactional(readOnly = true) (조회는 데이터를 안바꿈. 롤백 기능 필요 없음)
//JPA가 트랜잭션 없으면 DB 커넥션을 관리 못함. 그래서 조회도 트랜잭션이 필요함

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepo;
    private final TagRepository tagRepo;

    //게시글 저장
    @Transactional
    public Long createPost(PostRequestDto requestDto) {
        validateRequest(requestDto);

        Post post = new Post();
        post.setTitle(requestDto.getTitle());
        post.setContent(requestDto.getContent());

        if (requestDto.getCategoryIds() != null) {
            List<Category> categories = categoryRepo.findAllById(requestDto.getCategoryIds());
            post.getCategories().addAll(categories);
        }

        if (requestDto.getTagIds() != null) {
            List<Tag> tags = tagRepo.findAllById(requestDto.getTagIds());
            post.getTags().addAll(tags);
        }

        Post savedPost = postRepository.save(post);
        return savedPost.getPostsId();
    }

    //게시글 조회
    @Transactional
    public PostResponseDto getPost(Long id) {
        //조회수 증가
        postRepository.increaseViewCount(id);

        //글이 삭제된 상태에서 조회 요청 보낼 수 있고, URL에서 직접 요청하는 경우도 있으므로 예외처리
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다."));

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
    public PostResponseDto updatePost(Long id, PostRequestDto requestDto) {
        validateRequest(requestDto);

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다!"));

        post.setTitle(requestDto.getTitle());
        post.setContent(requestDto.getContent());

        return convertToResponseDto(post);
    }

    //게시글 삭제
    @Transactional
    public void deletePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다!"));

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
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setLikeCount(post.getLikeCount());
        dto.setDislikeCount(post.getDislikeCount());
        dto.setStatus(post.getStatus().name());
        dto.setCreateAt(post.getCreateAt());
        dto.setUpdatedAt(post.getUpdatedAt());
        dto.setViewCount(post.getViewCount());

        return dto;
    }

    //페이징 처리
    @Transactional(readOnly = true)
    public Page<PostResponseDto> getPostsByPage(int page, int size) {
        //몇 번째 페이지, 몇 개씩 가져올지, 글을 어떤 순서로 정렬할지 결정
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "postsId"));
        Page<Post> postPage = postRepository.findAll(pageable);

        //Post 엔티티 -> DTO
        return postPage.map(this::convertToResponseDto);
    }

    // 카테고리, 태그, 상태로 검색
    public List<Post> searchPosts(Long categoryId, Long tagId, Post.Status status) {
        return postRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            if (categoryId != null) {
                Join<Post, Category> catJoin = root.join("categories");
                predicates.add(cb.equal(catJoin.get("id"), categoryId));
            }

            if (tagId != null) {
                Join<Post, Tag> tagJoin = root.join("tags");
                predicates.add(cb.equal(tagJoin.get("id"), tagId));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        });
    }

    // 질문상태 업데이트
    public void updateStatus(Long postId, Post.Status status) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        post.setStatus(status);
        postRepository.save(post);
    }

}
