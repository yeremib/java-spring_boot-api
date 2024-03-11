package com.enigma.wmb_api.service;

import com.enigma.wmb_api.entity.UserCredential;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserCredentialService extends UserDetailsService {
    UserCredential getByUserId(String id);
    UserCredential getByContext();
    void deleteById(String id);

}
