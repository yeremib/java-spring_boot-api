package com.enigma.wmb_api.controller;

import com.enigma.wmb_api.constant.APIUrl;
import com.enigma.wmb_api.dto.request.NewBillRequest;
import com.enigma.wmb_api.dto.response.BillResponse;
import com.enigma.wmb_api.service.BillService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = APIUrl.BILL_API)
public class BillController {
    private final BillService billService;

    @PostMapping
    public BillResponse createNewBill(@RequestBody NewBillRequest billRequest) {
        return billService.create(billRequest);
    }

    @GetMapping
    public List<BillResponse> getAllBill() {
        return billService.getAll();
    }
}
