package com.enigma.wmb_api.service;

import com.enigma.wmb_api.dto.response.JwtClaims;
import com.enigma.wmb_api.entity.UserCredential;

public interface JwtService {
    String generateToken(UserCredential userCredential);
    boolean verifyJwtToken(String bearerToken);
    JwtClaims getClaimsByToken(String bearerToken);
}
