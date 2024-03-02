package com.enigma.wmb_api.repository;

import com.enigma.wmb_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<User, String> {
}
