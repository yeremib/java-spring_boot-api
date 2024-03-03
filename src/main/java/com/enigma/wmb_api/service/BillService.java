package com.enigma.wmb_api.service;

import com.enigma.wmb_api.entity.Bill;
import com.enigma.wmb_api.repository.BillRepository;

public interface BillService {
    BillResponse create(NewBillReq req);
    BillResponse getBillById(String id);
    Page<BillResponse> getAll(SearchBillReq req);
    Bill update(Bill bill);
}
