package com.enigma.wmb_api.service;

import com.enigma.wmb_api.dto.request.NewUserRequest;
import com.enigma.wmb_api.dto.request.SearchUserRequest;
import com.enigma.wmb_api.dto.request.UpdateUserRequest;
import com.enigma.wmb_api.dto.response.UserResponse;
import com.enigma.wmb_api.entity.User;
import org.springframework.data.domain.Page;

public interface UserService {
    User create(User user);
    User getOneById(String id);
    UserResponse getById(String id);
    Page<UserResponse> getAll(SearchUserRequest request);
    UserResponse update(UpdateUserRequest request);
    void delete(String id);
}
