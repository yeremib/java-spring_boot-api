package com.enigma.wmb_api.dto.response;

import com.enigma.wmb_api.entity.Role;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JwtClaims {
    private String email;
    private List<Role> roles;
}
