package com.enigma.wmb_api.service.impl;

import com.enigma.wmb_api.constant.TransactionType;
import com.enigma.wmb_api.entity.*;
import com.enigma.wmb_api.repository.BillDetailRepository;
import com.enigma.wmb_api.service.BillDetailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class BillDetailServiceImplTest {

    @Mock
    private BillDetailRepository billDetailRepository;

    private BillDetailService billDetailService;

    @BeforeEach
    void setUp() {
        billDetailService = new BillDetailServiceImpl(
                billDetailRepository
        );
    }

    @Test
    void shouldReturnBillDetailsWhenCreateBulk() {
        User user = User.builder().name("yeremi").build();
        TransType transType = TransType.builder().id(TransactionType.EI).description("Eat In").build();
        TableNum tableNum = TableNum.builder().name("T01").build();

        Bill bill = Bill.builder()
                .user(user)
                .tableNum(tableNum)
                .transType(transType)
                .transDate(new Date())
                .build();

        Menu menu = Menu.builder().name("Mie Ayam").build();

        BillDetail billDetail1 = BillDetail.builder()
                .bill(bill)
                .menu(menu)
                .qty(1)
                .build();

        BillDetail billDetail2 = BillDetail.builder()
                .bill(bill)
                .menu(menu)
                .qty(1)
                .build();

        List<BillDetail> billDetails = List.of(billDetail1, billDetail2);

        Mockito.when(billDetailRepository.saveAllAndFlush(billDetails)).thenReturn(billDetails);

        List<BillDetail> billDetailBulk = billDetailService.createBulk(billDetails);

        assertNotNull(billDetailBulk);
        assertEquals(2, billDetailBulk.size());

    }
}