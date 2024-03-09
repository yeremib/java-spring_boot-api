package com.enigma.wmb_api.service;

import com.enigma.wmb_api.dto.request.NewBillRequest;
import com.enigma.wmb_api.dto.request.SearchBillReq;
import com.enigma.wmb_api.dto.response.BillResponse;
import com.enigma.wmb_api.entity.Bill;
import org.springframework.data.domain.Page;

import java.util.List;


public interface BillService {
    Bill create(NewBillRequest req);
    Bill getBillById(String id);
    Page<Bill> getAll(SearchBillReq req);
}
