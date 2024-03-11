package com.enigma.wmb_api.service.impl;

import com.enigma.wmb_api.dto.request.UpdateUserRequest;
import com.enigma.wmb_api.entity.User;
import com.enigma.wmb_api.entity.UserCredential;
import com.enigma.wmb_api.service.UserCredentialService;
import com.enigma.wmb_api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthenticateUserServiceImpl {
    private final UserService userService;
    private final UserCredentialService userCredentialService;

    public boolean hasSameId(UpdateUserRequest request){
        User currentUser = userService.getOneById(request.getId());
        UserCredential userCredential = userCredentialService.getByContext();
        if(!userCredential.getId().equals(currentUser.getUserCredential().getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "not permitted");
        return true;
    }

}
