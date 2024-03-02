package com.enigma.wmb_api.repository;

import com.enigma.wmb_api.entity.UserCredential;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCredentialRepository extends JpaRepository<UserCredential, String> {
}
