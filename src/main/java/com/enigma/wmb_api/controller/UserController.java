package com.enigma.wmb_api.controller;

import com.enigma.wmb_api.constant.APIUrl;
import com.enigma.wmb_api.dto.request.NewUserRequest;
import com.enigma.wmb_api.dto.request.SearchUserRequest;
import com.enigma.wmb_api.dto.response.CommonResponse;
import com.enigma.wmb_api.dto.response.PagingResponse;
import com.enigma.wmb_api.entity.User;
import com.enigma.wmb_api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = APIUrl.USER_API)
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<CommonResponse<User>> createNewUser(@RequestBody User user) {
        User newUser = userService.create(user);
        CommonResponse<User> response = CommonResponse.<User>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("successfully create new user")
                .data(newUser)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<CommonResponse<User>> getUserById(@PathVariable String id) {
        User user = userService.getById(id);
        CommonResponse<User> response = CommonResponse.<User>builder()
                .statusCode(HttpStatus.OK.value())
                .message("user found")
                .data(user)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<CommonResponse<List<User>>> getAllUser(
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "size", defaultValue = "5") Integer size,
            @RequestParam(name = "sortBy", defaultValue = "name") String sortBy,
            @RequestParam(name = "direction", defaultValue = "asc") String direction,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "phoneNumber", required = false) String phoneNumber
    ) {
        SearchUserRequest searchUserRequest = SearchUserRequest.builder()
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .direction(direction)
                .name(name)
                .phoneNumber(phoneNumber)
                .build();

        Page<User> users = userService.getAll(searchUserRequest);

        PagingResponse pagingResponse = PagingResponse.builder()
                .totalPages(users.getTotalPages())
                .totalElement(users.getTotalElements())
                .page(users.getPageable().getPageNumber())
                .size(users.getPageable().getPageSize())
                .hasNext(users.hasNext())
                .hasPrevious(users.hasPrevious())
                .build();

        CommonResponse<List<User>> response = CommonResponse.<List<User>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("found all user")
                .data(users.getContent())
                .paging(pagingResponse)
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<CommonResponse<User>> updateUser(@RequestBody User user) {
        User user1 = userService.update(user);
        CommonResponse<User> response = CommonResponse.<User>builder()
                .statusCode(HttpStatus.OK.value())
                .message("user updated")
                .data(user1)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<CommonResponse<User>> deleteUser(@PathVariable String id) {
        User user = userService.getById(id);
        userService.delete(id);
        CommonResponse<User> response = CommonResponse.<User>builder()
                .statusCode(HttpStatus.OK.value())
                .message("user deleted")
                .data(user)
                .build();
        return  ResponseEntity.ok(response);
    }
}
