package com.enigma.wmb_api.controller;

import com.enigma.wmb_api.constant.APIUrl;
import com.enigma.wmb_api.dto.request.SearchUserRequest;
import com.enigma.wmb_api.dto.request.UpdateUserRequest;
import com.enigma.wmb_api.dto.response.CommonResponse;
import com.enigma.wmb_api.dto.response.PagingResponse;
import com.enigma.wmb_api.dto.response.UserResponse;
import com.enigma.wmb_api.entity.User;
import com.enigma.wmb_api.service.UserService;
import com.enigma.wmb_api.service.impl.AuthenticateUserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = APIUrl.USER_API)
public class UserController {
    private final UserService userService;
    private final AuthenticateUserServiceImpl authenticateUserService;


    @GetMapping(path = "/{id}")
    public ResponseEntity<CommonResponse<UserResponse>> getUserById(@PathVariable String id) {
        UserResponse user = userService.getById(id);
        CommonResponse<UserResponse> response = CommonResponse.<UserResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("user found")
                .data(user)
                .build();
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @GetMapping
    public ResponseEntity<CommonResponse<List<UserResponse>>> getAllUser(
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

        Page<UserResponse> users = userService.getAll(searchUserRequest);

        PagingResponse pagingResponse = PagingResponse.builder()
                .totalPages(users.getTotalPages())
                .totalElement(users.getTotalElements())
                .page(users.getPageable().getPageNumber() + 1)
                .size(users.getPageable().getPageSize())
                .hasNext(users.hasNext())
                .hasPrevious(users.hasPrevious())
                .build();

        CommonResponse<List<UserResponse>> response = CommonResponse.<List<UserResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("found all user")
                .data(users.getContent())
                .paging(pagingResponse)
                .build();

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN') OR @authenticateUserServiceImpl.hasSameId(#request)")
    @PutMapping
    public ResponseEntity<CommonResponse<UserResponse>> updateUser(@RequestBody UpdateUserRequest request) {
        UserResponse user1 = userService.update(request);
        CommonResponse<UserResponse> response = CommonResponse.<UserResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("user updated")
                .data(user1)
                .build();
        return ResponseEntity.ok(response);
    }
}
