package com.enigma.wmb_api.service.impl;


import com.enigma.wmb_api.dto.request.NewBillRequest;
import com.enigma.wmb_api.dto.request.SearchBillReq;
import com.enigma.wmb_api.dto.request.UpdateTransactionStatusRequest;
import com.enigma.wmb_api.dto.response.BillDetailResponse;
import com.enigma.wmb_api.dto.response.BillResponse;
import com.enigma.wmb_api.dto.response.PaymentResponse;
import com.enigma.wmb_api.entity.*;
import com.enigma.wmb_api.repository.BillRepository;
import com.enigma.wmb_api.service.*;
import com.enigma.wmb_api.specification.BillSpesification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BillServiceImpl implements BillService {
    private final BillRepository billRepository;
    private final BillDetailService billDetailService;
    private final UserService userService;
    private final TableNumService tableNumService;
    private final TransTypeService transTypeService;
    private final MenuService menuService;
    private final PaymentService paymentService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public BillResponse create(NewBillRequest req) {
        Bill bill;

        User user = userService.getOneById(req.getUserId());
        TransType transType = transTypeService.getById(req.getTransType());
        TableNum tableNum = null;
        String tableNumId = null;

        if (transType.getId().name().equals("EI")) {
            tableNum = tableNumService.getById(req.getTableId());
            tableNumId = tableNum.getId();
        }

        bill = Bill.builder()
                .user(user)
                .tableNum(tableNum)
                .transType(transType)
                .transDate(new Date())
                .build();

        billRepository.saveAndFlush(bill);

        List<BillDetail> billDetails = req.getBillDetails().stream().map(billDetailReq -> {
            Menu menu = menuService.getById(billDetailReq.getMenuId());
            return BillDetail.builder()
                    .bill(bill)
                    .menu(menu)
                    .qty(billDetailReq.getQty())
                    .price(menu.getPrice())
                    .build();
        }).toList();

        billDetailService.createBulk(billDetails);
        bill.setBillDetails(billDetails);

        List<BillDetailResponse> billDetailResponses = billDetails.stream().map(billDetail ->
                BillDetailResponse.builder()
                        .id(billDetail.getId())
                        .menuId(billDetail.getMenu().getId())
                        .price(billDetail.getPrice())
                        .qty(billDetail.getQty())
                        .build()).toList();

        Payment payment = paymentService.createPayment(bill);
        bill.setPayment(payment);

        PaymentResponse paymentResponse = PaymentResponse.builder()
                .id(payment.getId())
                .token(payment.getToken())
                .redirectUrl(payment.getRedirectUrl())
                .transactionStatus(payment.getTransactionStatus())
                .build();

        return BillResponse.builder()
                .id(bill.getId())
                .userId(bill.getUser().getId())
                .transDate(bill.getTransDate())
                .tableId(tableNumId)
                .transType(bill.getTransType().getId())
                .billDetails(billDetailResponses)
                .paymentResponse(paymentResponse)
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public Bill getBillById(String id) {
        Optional<Bill> bill = billRepository.findById(id);
        if(bill.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "bill not found");
        return bill.get();
    }

    @Transactional(readOnly = true)
    @Override
    public Page<BillResponse> getAll(SearchBillReq req) {
        if (req.getPage() <= 0) req.setPage(1);
        Sort sort = Sort.by(Sort.Direction.fromString(req.getDirection()), req.getSortBy());
        Pageable pageable = PageRequest.of(req.getPage() - 1, req.getSize(), sort);
        Specification<Bill> specification = BillSpesification.getSpesification(req);
        Page<Bill> bills =  billRepository.findAll(specification, pageable);

        return bills.map(bill -> {
            String tableNumId = null;

            if (bill.getTransType().equals("EI")) tableNumId = bill.getTableNum().getId();

            List<BillDetailResponse> billDetailResponses = bill.getBillDetails().stream().map(billDetail ->
                    BillDetailResponse.builder()
                            .id(billDetail.getId())
                            .menuId(billDetail.getMenu().getId())
                            .price(billDetail.getPrice())
                            .qty(billDetail.getQty())
                            .build()).toList();

            PaymentResponse paymentResponse = PaymentResponse.builder()
                    .id(bill.getPayment().getId())
                    .transactionStatus(bill.getPayment().getTransactionStatus())
                    .build();

            return BillResponse.builder()
                    .id(bill.getId())
                    .transDate(bill.getTransDate())
                    .userId(bill.getUser().getId())
                    .tableId(tableNumId)
                    .transType(bill.getTransType().getId())
                    .billDetails(billDetailResponses)
                    .paymentResponse(paymentResponse)
                    .build();
        });
    }

    @Override
    public List<Bill> getAllBill() {
        return billRepository.findAll();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateStatus(UpdateTransactionStatusRequest request) {
        Bill bill = billRepository.findById(request.getOrderId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "bill not found"));
        Payment payment = bill.getPayment();
        payment.setTransactionStatus(request.getTransactionStatus());
    }
}
