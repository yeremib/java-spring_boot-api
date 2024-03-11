package com.enigma.wmb_api.dto.response;

import com.enigma.wmb_api.constant.TransactionType;
import com.enigma.wmb_api.entity.BillDetail;
import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BillResponse {
    private String id;
    private Date transDate;
    private String userId;
    private String tableId;
    private TransactionType transType;
    private List<BillDetailResponse>  billDetails;
    private PaymentResponse paymentResponse;
    private PagingResponse pagingResponse;
}
