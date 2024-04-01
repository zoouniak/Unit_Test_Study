package com.example.unitTestStudy.user.service;

import com.example.unitTestStudy.user.converter.UserConverter;
import com.example.unitTestStudy.user.domain.User;
import com.example.unitTestStudy.user.dto.UserCreateRequestDto;
import com.example.unitTestStudy.user.dto.UserResponseDto;
import com.example.unitTestStudy.user.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService sut;

    @Mock
    private UserRepository userRepository;

    @Spy
    private UserConverter userConverter;


    @Test
    @DisplayName("멤버 정보를 데이터베이스에 저장한다.")
    void Save_member_to_DB() {
        User user = new User("Seyeon", 23, "Coding");
        UserCreateRequestDto requestDto = UserCreateRequestDto.builder()
                .name(user.getName())
                .age(user.getAge())
                .hobby(user.getHobby())
                .build();
        Mockito.when(userRepository.save(userConverter.toUser(requestDto))).thenReturn(user);

        Long save = sut.save(requestDto);

        Assertions.assertThat(save).isEqualTo(userConverter.toUserResponseDto(user).getId());


    }

    @Test
    @DisplayName("id로 멤버 조회에 성공한다.")
    void Find_member_by_id() {
        User savedUser = createUserRequest();
        Mockito.when(userRepository.findById(savedUser.getId())).thenReturn(Optional.of(savedUser));

        UserResponseDto findUser = sut.findOne(savedUser.getId());

        assertThat(findUser.getId()).isEqualTo(savedUser.getId());
    }

    // 팩토리 메서드로 테스트 픽스처 생성
     private User createUserRequest(){
        UserCreateRequestDto requestDto = UserCreateRequestDto.builder()
                .name("Seyeon")
                .age(20)
                .hobby("coding")
                .build();
        return userConverter.toUser(requestDto);
    }
}