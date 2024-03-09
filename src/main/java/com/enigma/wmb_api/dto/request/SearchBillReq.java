package com.enigma.wmb_api.dto.request;

import com.enigma.wmb_api.entity.TransType;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchBillReq {
    private Integer page;
    private Integer size;
    private String sortBy;
    private String direction;
    private String afterDate;
    private String beforeDate;
    private String transType;
}
