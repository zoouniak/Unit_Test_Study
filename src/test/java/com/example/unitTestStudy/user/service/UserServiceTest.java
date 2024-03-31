package com.example.unitTestStudy.user.service;

import com.example.unitTestStudy.user.domain.User;
import com.example.unitTestStudy.user.dto.UserCreateRequestDto;
import com.example.unitTestStudy.user.dto.UserResponseDto;
import com.example.unitTestStudy.user.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService sut;

    @Autowired
    private UserRepository userRepository;


    @Test
    @DisplayName("멤버 정보를 데이터베이스에 저장한다.")
    void Save_member_to_DB() {
        User user = new User("Seyeon", 23, "Coding");
        UserCreateRequestDto requestDto = UserCreateRequestDto.builder()
                .name(user.getName())
                .age(user.getAge())
                .hobby(user.getHobby())
                .build();

        Long savedId = sut.save(requestDto);

        assertThat(savedId).isEqualTo(1);
        Assertions.assertThat(user.getName()).isEqualTo(userRepository.findById(savedId).get().getName());
    }

    @Test
    @DisplayName("id로 멤버 조회에 성공한다.")
    void Find_member_by_id() {

        UserCreateRequestDto requestDto = UserCreateRequestDto.builder()
                .name("member1")
                .age(10)
                .build();
        Long savedId = sut.save(requestDto);

        UserResponseDto responseDto = sut.findOne(savedId);

        assertThat(responseDto.getName()).isEqualTo(requestDto.getName());
        System.out.println("responseDto.getName() = " + responseDto.getName() + " requestDto.getName() = "+requestDto.getName());
    }
}