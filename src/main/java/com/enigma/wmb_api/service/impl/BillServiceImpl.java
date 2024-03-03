package com.enigma.wmb_api.service.impl;

import com.enigma.wmb_api.constant.TransactionType;
import com.enigma.wmb_api.dto.request.NewBillRequest;
import com.enigma.wmb_api.dto.response.BillDetailResponse;
import com.enigma.wmb_api.dto.response.BillResponse;
import com.enigma.wmb_api.entity.*;
import com.enigma.wmb_api.repository.BillRepository;
import com.enigma.wmb_api.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BillServiceImpl implements BillService {
    private final BillRepository billRepository;
    private final BillDetailService billDetailService;
    private final UserService userService;
    private final TableNumService tableNumService;
    private final TransTypeService transTypeService;
    private final MenuService menuService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public BillResponse create(NewBillRequest req) {
        Bill bill;
        BillResponse response;

        User user = userService.getById(req.getUserId());
        TransType transType = transTypeService.getById(req.getTransType());

        if (transType.getId().name().equals("TA")) {
            bill = Bill.builder()
                    .user(user)
                    .transType(transType)
                    .transDate(new Date())
                    .build();
            billRepository.saveAndFlush(bill);
        } else {
            TableNum tableNum = tableNumService.getById(req.getTableId());
            bill = Bill.builder()
                    .user(user)
                    .tableNum(tableNum)
                    .transType(transType)
                    .transDate(new Date())
                    .build();
            billRepository.saveAndFlush(bill);
        }

        List<BillDetail> billDetails = req.getBillDetails().stream().map(billDetailReq -> {
            Menu menu = menuService.getById(billDetailReq.getMenuId());
            return BillDetail.builder()
                    .bill(bill)
                    .menu(menu)
                    .qty(billDetailReq.getQty())
                    .price(billDetailReq.getPrice())
                    .build();
        }).toList();

        billDetailService.createBulk(billDetails);
        bill.setBillDetails(billDetails);

        List<BillDetailResponse> billDetailResponses = billDetails.stream().map(billDetail -> {
            return BillDetailResponse.builder()
                    .id(billDetail.getId())
                    .menuId(billDetail.getMenu().getId())
                    .qty(billDetail.getQty())
                    .price(billDetail.getPrice())
                    .build();
        }).toList();

        if(transType.getId().name().equals("TA")) {
            response = BillResponse.builder()
                    .id(bill.getId())
                    .userId(bill.getUser().getId())
                    .transType(bill.getTransType().getId())
                    .transDate(bill.getTransDate())
                    .billDetails(billDetailResponses)
                    .build();
        } else {
            response = BillResponse.builder()
                    .id(bill.getId())
                    .userId(bill.getUser().getId())
                    .tableId(bill.getTableNum().getId())
                    .transType(bill.getTransType().getId())
                    .transDate(bill.getTransDate())
                    .billDetails(billDetailResponses)
                    .build();
        }

        return response;
    }

    @Override
    public BillResponse getBillById(String id) {
        return null;
    }

    @Override
    public List<BillResponse> getAll() {
        List<Bill> bills = billRepository.findAll();

        return bills.stream().map(bill -> {
            List<BillDetailResponse> billDetailResponses = bill.getBillDetails().stream().map(billDetail -> {
                return BillDetailResponse.builder()
                        .id(billDetail.getId())
                        .menuId(billDetail.getMenu().getId())
                        .qty(billDetail.getQty())
                        .price(billDetail.getPrice())
                        .build();
            }).toList();

            if(bill.getTransType().getId().equals(TransactionType.TA)) {
                return BillResponse.builder()
                        .id(bill.getId())
                        .transDate(bill.getTransDate())
                        .userId(bill.getUser().getId())
                        .transType(bill.getTransType().getId())
                        .billDetails(billDetailResponses)
                        .build();
            } else {
                return BillResponse.builder()
                        .id(bill.getId())
                        .transDate(bill.getTransDate())
                        .userId(bill.getUser().getId())
                        .tableId(bill.getTableNum().getId())
                        .transType(bill.getTransType().getId())
                        .billDetails(billDetailResponses)
                        .build();
            }
        }).toList();
    }
}
