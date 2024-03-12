package com.enigma.wmb_api.service.impl;

import com.enigma.wmb_api.dto.request.SearchUserRequest;
import com.enigma.wmb_api.dto.request.UpdateUserRequest;
import com.enigma.wmb_api.dto.response.UserResponse;
import com.enigma.wmb_api.entity.User;
import com.enigma.wmb_api.entity.UserCredential;
import com.enigma.wmb_api.repository.UserRepository;
import com.enigma.wmb_api.service.UserCredentialService;
import com.enigma.wmb_api.service.UserService;
import com.enigma.wmb_api.specification.UserSpecification;
import com.enigma.wmb_api.util.ValidationUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ValidationUtil validationUtil;
    @Mock
    private UserCredentialService userCredentialService;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(
                userRepository,
                validationUtil,
                userCredentialService
        );
    }

    @Test
    void shouldReturnUserWhenCreateSuccessfully() {
        User user = User.builder()
                .name("Budi")
                .phoneNumber("0899556578")
                .status(true)
                .userCredential(UserCredential.builder().build())
                .build();

        Mockito.doNothing().when(validationUtil).validate(user);
        Mockito.when(userRepository.saveAndFlush(user)).thenReturn(user);

        User actualUser = userService.create(user);

        assertEquals("Budi", actualUser.getName());
        assertEquals("0899556578", actualUser.getPhoneNumber());
    }

    @Test
    void shouldReturnUserWhenGetOneById() {
        String id = "userId-1";

        User mockUser = User.builder()
                .id("userId-1")
                .name("Budi")
                .phoneNumber("0899556578")
                .status(true)
                .userCredential(UserCredential.builder().build())
                .build();

        Mockito.when(userRepository.findById(id)).thenReturn(Optional.ofNullable(mockUser));

        User actualUser = userService.getOneById(id);

        assertNotNull(actualUser);
        assertEquals("Budi", actualUser.getName());
        assertEquals("userId-1", actualUser.getId());
    }

    @Test
    void shouldReturnUserResponseWhenGetById() {
        String id = "userId-1";

        User mockUser = User.builder()
                .id("userId-1")
                .name("Budi")
                .phoneNumber("0899556578")
                .status(true)
                .userCredential(UserCredential.builder().build())
                .build();

        Mockito.when(userRepository.findById(id)).thenReturn(Optional.ofNullable(mockUser));

        UserResponse actualUserResponse = userService.getById(id);

        assertEquals("Budi", actualUserResponse.getName());
        Mockito.verify(userRepository, Mockito.times(1)).findById(id);
    }

    @Test
    void shouldReturnUsersWhenGetAll() {
        SearchUserRequest searchUserRequest = SearchUserRequest.builder()
                .page(0)
                .size(2)
                .sortBy("name")
                .direction("asc")
                .build();

        User mockUser1 = User.builder()
                .id("userId-1")
                .name("Budi")
                .phoneNumber("0899556578")
                .status(true)
                .userCredential(UserCredential.builder().build())
                .build();

        User mockUser2 = User.builder()
                .id("userId-2")
                .name("Deni")
                .phoneNumber("08995556478")
                .status(true)
                .userCredential(UserCredential.builder().build())
                .build();
        List<User> mockUsers = List.of(mockUser1, mockUser2);

        Page<User> users = new PageImpl<>(mockUsers);

        Mockito.when(userRepository.findAll(Mockito.any(Specification.class),Mockito.any(Pageable.class))).thenReturn(users);

        Page<UserResponse> userResponses = userService.getAll(searchUserRequest);

        assertNotNull(userResponses);
        assertEquals(2, userResponses.getSize());
    }

    @Test
    void shouldReturnUserWhenUpdate() {
        UpdateUserRequest request = UpdateUserRequest.builder()
                .id("userId-1")
                .name("Budi")
                .phoneNumber("0899556578")
                .build();

        User mockUser = User.builder()
                .name("Andi")
                .phoneNumber("0899556578")
                .status(true)
                .userCredential(UserCredential.builder().build())
                .build();

        Mockito.when(userRepository.findById(request.getId())).thenReturn(Optional.ofNullable(mockUser));
        mockUser.setName(request.getName());
        mockUser.setPhoneNumber(request.getPhoneNumber());
        Mockito.when(userRepository.saveAndFlush(mockUser)).thenReturn(mockUser);

        UserResponse userResponse = userService.update(request);

        assertEquals("Budi", userResponse.getName());
    }
}