package com.enigma.wmb_api.dto.request;

import com.enigma.wmb_api.entity.BillDetail;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewBillRequest {
    @NotBlank(message = "user id is required")
    private String userId;

    @NotBlank(message = "table id is required")
    private String tableId;

    @NotBlank(message = "transaction type is required")
    private String transType;

    private List<NewBillDetailRequest> billDetails;
}
