package com.enigma.wmb_api.dto.request;

import com.enigma.wmb_api.entity.UserCredential;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewUserRequest {
    @NotBlank(message = "name is required")
    private String name;

    private String phoneNumber;

    private Boolean status;
    private UserCredential userCredential;
}
