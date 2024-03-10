package com.enigma.wmb_api.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewMenuRequest {
    @NotBlank(message = "menu name is required")
    private String name;

    @NotNull(message = "price is required")
    @Min(value = 0, message = "price must be greater than or equals to 0")
    private Integer price;

    private MultipartFile image;
}
