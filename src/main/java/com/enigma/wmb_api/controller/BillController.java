package com.enigma.wmb_api.controller;

import com.enigma.wmb_api.constant.APIUrl;
import com.enigma.wmb_api.dto.request.NewBillRequest;
import com.enigma.wmb_api.dto.request.SearchBillReq;
import com.enigma.wmb_api.dto.response.CommonResponse;
import com.enigma.wmb_api.dto.response.PagingResponse;
import com.enigma.wmb_api.entity.Bill;
import com.enigma.wmb_api.service.BillService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = APIUrl.BILL_API)
public class BillController {
    private final BillService billService;


    @PostMapping
    public ResponseEntity<CommonResponse<Bill>> createNewBill(@RequestBody NewBillRequest billRequest) {
       Bill bill = billService.create(billRequest);

       CommonResponse<Bill> response = CommonResponse.<Bill>builder()
               .statusCode(HttpStatus.CREATED.value())
               .message("successfully create new bill")
               .data(bill)
               .build();

       return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN') OR #id == @userCredentialServiceImpl.getByUserId()")
    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<Bill>> getBillById(@PathVariable String id) {
        Bill bill = billService.getBillById(id);

        CommonResponse<Bill> response = CommonResponse.<Bill>builder()
                .statusCode(HttpStatus.OK.value())
                .message("successfully find bill by id")
                .data(bill)
                .build();

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @GetMapping
    public ResponseEntity<CommonResponse<List<Bill>>> getAllBill(
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "size", defaultValue = "2") Integer size,
            @RequestParam(name = "sortBy", defaultValue = "transDate") String sortBy,
            @RequestParam(name = "direction", defaultValue = "asc") String direction,
            @RequestParam(name = "transType", required = false) String transType,
            @RequestParam(name = "afterDate", required = false) String afterDate,
            @RequestParam(name = "beforeDate", required = false) String beforeDate
    ) {
        SearchBillReq req = SearchBillReq.builder()
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .direction(direction)
                .transType(transType)
                .afterDate(afterDate)
                .beforeDate(beforeDate)
                .build();

        Page<Bill> bills = billService.getAll(req);

        PagingResponse pagingResponse = PagingResponse.builder()
                .totalPages(bills.getTotalPages())
                .totalElement(bills.getTotalElements())
                .page(bills.getPageable().getPageNumber() + 1)
                .size(bills.getPageable().getPageSize())
                .hasNext(bills.hasNext())
                .hasPrevious(bills.hasPrevious())
                .build();

        CommonResponse<List<Bill>> response = CommonResponse.<List<Bill>> builder()
                .statusCode(HttpStatus.OK.value())
                .message("successfully get all menu")
                .data(bills.getContent())
                .paging(pagingResponse)
                .build();

        return ResponseEntity.ok(response);
    }
}
