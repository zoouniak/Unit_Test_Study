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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostConverter postConverter;

    private final int PAGE_SIZE;

    public PostService(PostRepository postRepository,
                       UserRepository userRepository,
                       PostConverter postConverter) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.postConverter = postConverter;
        this.PAGE_SIZE=10;

    }

    // todo exception code enum 추가
    @Transactional
    public Long save(PostCreateRequestDto postCreateRequestDto) {
        User user = userRepository.findById(postCreateRequestDto.getUserId())
                .orElseThrow(() -> new NotFoundException(ExceptionCode.MEMBER_NOT_FOUND));
        Post post = postConverter.toPost(postCreateRequestDto, user);
        Post entity = postRepository.save(post);
        return entity.getId();
    }

    @Transactional(readOnly = true)
    public PostResponseDto getPost(Long postId) {
        return postRepository.findById(postId)
                .map(postConverter::toPostResponseDto)
                .orElseThrow(() -> new NotFoundException(ExceptionCode.POST_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Page<PostResponseDto> getAllPosts(int page) {
        PageRequest pageRequest = PageRequest.of(page, PAGE_SIZE,
                Sort.by(DESC, "createdAt"));
        return postRepository.findAll(pageRequest)
                .map(postConverter::toPostResponseDto);
    }

    // todo list dto로 바꿔보기
    @Transactional(readOnly = true)
    public Page<PostResponseDto> getAllPostsByUserId(Long userId, int page) {
        PageRequest pageRequest = PageRequest.of(page, PAGE_SIZE,
                Sort.by(DESC, "createdAt"));
        return postRepository.findPostsByUserId(userId, pageRequest)
                .map(postConverter::toPostResponseDto);
    }

    @Transactional
    public Long edit(PostEditRequestDto postEditRequestDto) {
        Post post = postRepository.findById(postEditRequestDto.getPostId())
                .orElseThrow(() -> new NotFoundException(ExceptionCode.POST_NOT_FOUND));
        post.changeTitle(postEditRequestDto.getTitle());
        post.changeContent(postEditRequestDto.getContent());
        return post.getId();
    }

    @Transactional
    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException(ExceptionCode.POST_NOT_FOUND));
        postRepository.deleteById(postId);
    }
}