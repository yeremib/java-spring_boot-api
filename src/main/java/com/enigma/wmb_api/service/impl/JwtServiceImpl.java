package com.enigma.wmb_api.service.impl;

import com.enigma.wmb_api.dto.response.JwtClaims;
import com.enigma.wmb_api.entity.UserCredential;
import com.enigma.wmb_api.service.JwtService;

public class JwtServiceImpl implements JwtService {
    @Override
    public String generateToken(UserCredential userCredential) {
        return null;
    }

    @Override
    public boolean verifyJwtToken(String token) {
        return false;
    }

    @Override
    public JwtClaims getClaimsByToken(String token) {
        return null;
    }
}
