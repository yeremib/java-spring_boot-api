package com.enigma.wmb_api.service.impl;

import com.enigma.wmb_api.dto.request.NewUserRequest;
import com.enigma.wmb_api.dto.request.SearchUserRequest;
import com.enigma.wmb_api.entity.User;
import com.enigma.wmb_api.repository.UserRepository;
import com.enigma.wmb_api.service.UserService;
import com.enigma.wmb_api.specification.UserSpecification;
import com.enigma.wmb_api.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ValidationUtil validationUtil;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public User create(User user) {
        validationUtil.validate(user);
        return userRepository.saveAndFlush(user);
    }

    @Transactional(readOnly = true)
    @Override
    public User getById(String id) {
        Optional<User> user = userRepository.findById(id);
        if(user.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found");
        return user.get();
    }

    @Transactional(readOnly = true)
    @Override
    public Page<User> getAll(SearchUserRequest request) {
        if(request.getPage() <= 0) request.setPage(1);
        Sort sort = Sort.by(Sort.Direction.fromString(request.getDirection()), request.getSortBy());
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize(), sort);
        Specification<User> specification = UserSpecification.getSpesification(request);
        return userRepository.findAll(specification, pageable);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public User update(User user) {
        getById(user.getId());
        return userRepository.saveAndFlush(user);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(String id) {
        User currUser = getById(id);
        userRepository.delete(currUser);
    }
}
