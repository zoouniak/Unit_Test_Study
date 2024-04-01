package com.example.unitTestStudy.post.service;

import com.example.unitTestStudy.common.exception.NotFoundException;
import com.example.unitTestStudy.post.converter.PostConverter;
import com.example.unitTestStudy.post.domain.Post;
import com.example.unitTestStudy.post.dto.PostCreateRequestDto;
import com.example.unitTestStudy.post.dto.PostEditRequestDto;
import com.example.unitTestStudy.post.dto.PostResponseDto;
import com.example.unitTestStudy.post.repository.PostRepository;
import com.example.unitTestStudy.user.converter.UserConverter;
import com.example.unitTestStudy.user.domain.User;
import com.example.unitTestStudy.user.dto.UserCreateRequestDto;
import com.example.unitTestStudy.user.repository.UserRepository;
import com.example.unitTestStudy.user.service.UserService;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.annotation.Rollback;

import java.lang.reflect.Member;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith({MockitoExtension.class})
class PostServiceTest {

    @InjectMocks
    private PostService sut;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Spy
    private PostConverter postConverter;


    @Test
    @DisplayName("포스트 저장에 성공한다.")
    void save_the_Post() {
        User member = createMember();
        Post post = createPost();
        PostCreateRequestDto requestDto = createPostDto(post, member);
        Mockito.when(postRepository.save(any())).thenReturn(post);
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(member));

        Long savedId = sut.save(requestDto);

        Assertions.assertThat(savedId).isEqualTo(post.getId());
    }

    @Test
    @DisplayName("postId로 포스트를 조회하는 것을 성공한다.")
    void get_the_Post() {
        User member = createMember();
        Post post = createPost();
        PostCreateRequestDto requestDto = createPostDto(post, member);
        Post toPost = postConverter.toPost(requestDto,member);
        Long postId = 1L;
        Mockito.when(postRepository.findById(postId)).thenReturn(Optional.of(toPost));

        PostResponseDto responseDto = sut.getPost(postId);

        Assertions.assertThat(responseDto.getTitle()).isEqualTo(toPost.getTitle());
    }

//    @Test
//    @DisplayName("DB에 저장된 모든 포스트를 조회하는 것을 성공한다.")
//    @Transactional
//    void getAllPosts() {
//        Long member1 = createMember("2");
//        savePost(member1);
//
//        Long member2 = createMember("4");
//        savePost(member2);
//
//        Page<PostResponseDto> allPosts = sut.getAllPosts(1);
//
//        Assertions.assertThat(allPosts.getTotalElements()).isEqualTo(3);
//    }

//    @Test
//    @DisplayName("userId 로 모든 포스트를 조회하는 것을 성공한다.")
//    void getAllPostsByUserId() {
//
//        Long member = createMember("1");
//        savePost(member);
//
//        Page<PostResponseDto> posts = sut.getAllPostsByUserId(member, 1);
//
//        Assertions.assertThat(posts.getTotalElements()).isEqualTo(1);
//    }
//
//    @Test
//    @DisplayName("포스트 변경에 성공한다.")
//    void edit() {
//        Post post = createPost();
//        Long member = createMember("3");
//        savePost(member);
//        PostEditRequestDto requestDto = PostEditRequestDto.builder()
//                .postId(1L)
//                .title(post.getTitle())
//                .content(post.getContent())
//                .build();
//
//        Long edit = sut.edit(requestDto);
//
//        Assertions.assertThat(edit).isEqualTo(1L);
//    }
//
//    @Test
//    @DisplayName("포스트 삭제에 성공하면, 해당 postId로 포스트를 찾는 것에 실패한다.")
//    void deletePost() {
//        //Arrange
//        Long member = createMember("5");
//        savePost(member);
//
//        //Act
//        sut.deletePost(1L);
//
//        //Assert
//        Assertions.assertThatThrownBy(() -> postRepository.findById(1L).orElseThrow())
//                .isInstanceOf(Exception.class);
//    }
//
//
//    팩토리 메서드로 멤버
    private User createMember(){
        User user = new User("Seyeon", 20, "coding");

       return user;
    }

    private static Post createPost(){
        return new Post("제목", "내용");
    }

    private static PostCreateRequestDto createPostDto(Post post, User member){
        return PostCreateRequestDto.builder()
                .userId(member.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .build();
    }
}