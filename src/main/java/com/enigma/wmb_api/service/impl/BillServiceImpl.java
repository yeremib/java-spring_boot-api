package com.enigma.wmb_api.service.impl;


import com.enigma.wmb_api.dto.request.NewBillRequest;
import com.enigma.wmb_api.dto.request.SearchBillReq;
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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Bill create(NewBillRequest req) {
        Bill bill;

        User user = userService.getById(req.getUserId());
        TransType transType = transTypeService.getById(req.getTransType());
        TableNum tableNum = tableNumService.getById(req.getTableId());

        if (transType.getId().name().equals("TA")) {
            bill = Bill.builder()
                    .user(user)
                    .transType(transType)
                    .transDate(new Date())
                    .build();
        } else {
            bill = Bill.builder()
                    .user(user)
                    .tableNum(tableNum)
                    .transType(transType)
                    .transDate(new Date())
                    .build();
        }

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

        return bill;
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
    public Page<Bill> getAll(SearchBillReq req) {
        if (req.getPage() <= 0) req.setPage(1);
        Sort sort = Sort.by(Sort.Direction.fromString(req.getDirection()), req.getSortBy());
        Pageable pageable = PageRequest.of(req.getPage() - 1, req.getSize(), sort);
        Specification<Bill> specification = BillSpesification.getSpesification(req);
        return billRepository.findAll(specification, pageable);
    }
}
