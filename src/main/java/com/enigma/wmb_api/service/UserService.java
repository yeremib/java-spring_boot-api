package com.enigma.wmb_api.service;

import com.enigma.wmb_api.dto.request.NewUserRequest;
import com.enigma.wmb_api.dto.request.SearchUserRequest;
import com.enigma.wmb_api.entity.User;
import org.springframework.data.domain.Page;

public interface UserService {
    User create(NewUserRequest request);
    User getById(String id);
    Page<User> getAll(SearchUserRequest request);
    User update(User user);
    void delete(String id);
}
