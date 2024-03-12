package com.enigma.wmb_api.controller;

import com.enigma.wmb_api.constant.APIUrl;
import com.enigma.wmb_api.dto.request.NewBillRequest;
import com.enigma.wmb_api.dto.request.SearchBillReq;
import com.enigma.wmb_api.dto.request.UpdateTransactionStatusRequest;
import com.enigma.wmb_api.dto.response.BillResponse;
import com.enigma.wmb_api.dto.response.CommonResponse;
import com.enigma.wmb_api.dto.response.PagingResponse;
import com.enigma.wmb_api.entity.Bill;
import com.enigma.wmb_api.service.BillService;
import com.enigma.wmb_api.util.BillPdfExporter;
import com.enigma.wmb_api.util.CsvGeneratorUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = APIUrl.BILL_API)
public class BillController {
    private final BillService billService;
    private final CsvGeneratorUtil csvGeneratorUtil;


    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CommonResponse<BillResponse>> createNewBill(@RequestBody NewBillRequest billRequest) {
       BillResponse bill = billService.create(billRequest);

       CommonResponse<BillResponse> response = CommonResponse.<BillResponse>builder()
               .statusCode(HttpStatus.CREATED.value())
               .message("successfully create new bill")
               .data(bill)
               .build();

       return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
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
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse<List<BillResponse>>> getAllBill(
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size,
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

        Page<BillResponse> bills = billService.getAll(req);

        PagingResponse pagingResponse = PagingResponse.builder()
                .totalPages(bills.getTotalPages())
                .totalElement(bills.getTotalElements())
                .page(bills.getPageable().getPageNumber() + 1)
                .size(bills.getPageable().getPageSize())
                .hasNext(bills.hasNext())
                .hasPrevious(bills.hasPrevious())
                .build();

        CommonResponse<List<BillResponse>> response = CommonResponse.<List<BillResponse>> builder()
                .statusCode(HttpStatus.OK.value())
                .message("successfully get all menu")
                .data(bills.getContent())
                .paging(pagingResponse)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/status")
    public ResponseEntity<CommonResponse<?>> updateStatus(@RequestBody Map<String, Object> request) {
        UpdateTransactionStatusRequest updateTransactionStatusRequest = UpdateTransactionStatusRequest.builder()
                .orderId(request.get("order_id").toString())
                .transactionStatus(request.get("transaction_status").toString())
                .build();
        billService.updateStatus(updateTransactionStatusRequest);
        return ResponseEntity.ok(CommonResponse.builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("successfully update transaction status")
                .build());
    }

    @GetMapping(path = "/csv")
    public ResponseEntity<byte[]> generateCsvFile() {
        List<Bill> bills = billService.getAllBill();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "bills.csv");
        byte[] csvBytes =  csvGeneratorUtil.generateCsv(bills).getBytes();

        return new ResponseEntity<>(csvBytes, headers, HttpStatus.OK);
    }

    @GetMapping(path = "/pdf")
    public void exportToPdf(HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");

        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=bills_" + currentDateTime + ".pdf";

        response.setHeader(headerKey, headerValue);

        List<Bill> listBill = billService.getAllBill();

        BillPdfExporter exporter = new BillPdfExporter(listBill);
        exporter.export(response);
    }
}
