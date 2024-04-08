package com.example.unitTestStudy.post.service;

import com.example.unitTestStudy.common.exception.ExceptionCode;
import com.example.unitTestStudy.common.exception.NotFoundException;
import com.example.unitTestStudy.post.converter.PostConverter;
import com.example.unitTestStudy.post.domain.Post;
import com.example.unitTestStudy.post.dto.PostCreateRequestDto;
import com.example.unitTestStudy.post.dto.PostEditRequestDto;
import com.example.unitTestStudy.post.dto.PostResponseDto;
import com.example.unitTestStudy.post.repository.PostRepository;
import com.example.unitTestStudy.user.domain.User;
import com.example.unitTestStudy.user.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    @InjectMocks
    private PostService sut;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PostRepository postRepository;
    @Spy
    private PostConverter postConverter;

    @Test
    void 게시물을_저장한다() {
        PostCreateRequestDto req = createRequestDto();

        User user = createUser();

        given(userRepository.findById(req.getUserId())).willReturn(Optional.of(user));
        Post post = new Post(1L, "title", "content", user);
        given(postRepository.save(any())).willReturn(post);

        Long save = sut.save(req);

        Assertions.assertEquals(1L, save);
    }

    @Test
    void 사용자가_존재하지_않으면_게시물을_저장할_수_없다() {
        PostCreateRequestDto req = createRequestDto();
        given(userRepository.findById(req.getUserId())).willReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> sut.save(req));

        Assertions.assertEquals(ExceptionCode.MEMBER_NOT_FOUND, exception.getCode());
    }

    private PostCreateRequestDto createRequestDto() {
        return PostCreateRequestDto.builder()
                .userId(1L)
                .title("testTitle")
                .content("testContent")
                .build();
    }

    @Test
    void 게시물을_조회한다() {
        when(postRepository.findById(any())).thenReturn(Optional.of(createPost(1L)));

        PostResponseDto response = sut.getPost(1L);

        Assertions.assertEquals(1L, response.getId());
    }

    @Test
    void 게시물이_없으면_조회할_수_없다() {
        when(postRepository.findById(any())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> sut.getPost(1L));

        Assertions.assertEquals(ExceptionCode.POST_NOT_FOUND, exception.getCode());
    }

    @Test
    void 페이지에_따른_전체_게시물을_조회한다() {
        List<Post> posts = new ArrayList<>();
        posts.add(createPost(1L));
        posts.add(createPost(2L));
        Page<Post> pageOfPosts = new PageImpl<>(posts);
        int page = 1;
        int pageSize = 10;
        when(postRepository.findAll(PageRequest.of(page, pageSize,
                Sort.by(Sort.Direction.DESC, "createdAt")))).thenReturn(pageOfPosts);

        Page<PostResponseDto> results = sut.getAllPosts(page);

        Assertions.assertEquals(1L, results.getContent().get(0).getId());
        Assertions.assertEquals(2L, results.getContent().get(1).getId());
    }

    @Test
    void 게시물을_수정한다() {
        Post post = createPost(1L);
        PostEditRequestDto req = createEditDto();
        when(postRepository.findById(any())).thenReturn(Optional.of(post));

        sut.edit(req);

        Assertions.assertEquals("editTitle", post.getTitle());
        Assertions.assertEquals("editContent", post.getContent());
    }

    private static PostEditRequestDto createEditDto() {
        return new PostEditRequestDto(1L, "editTitle", "editContent");
    }

    @Test
    void 존재하지_않는_게시물은_수정할_수_없다() {
        PostEditRequestDto req = createEditDto();
        when(postRepository.findById(any())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> sut.edit(req));

        Assertions.assertEquals(ExceptionCode.POST_NOT_FOUND, exception.getCode());
    }

    @Test
    void 게시물을_삭제한다() {
        Post post = createPost(1L);
        when(postRepository.findById(any())).thenReturn(Optional.of(post));

        sut.deletePost(1L);

        verify(postRepository).deleteById(1L);
    }

    @Test
    void 존재하지_않는_게시물은_삭제할_수_없다() {
        when(postRepository.findById(any())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> sut.deletePost(1L));

        Assertions.assertEquals(ExceptionCode.POST_NOT_FOUND, exception.getCode());
    }

    private static Post createPost(long id) {
        User user = createUser();
        return new Post(id, "title", "content", user);
    }

    private static User createUser() {
        return new User(1L, "test", 23, "test");
    }


}