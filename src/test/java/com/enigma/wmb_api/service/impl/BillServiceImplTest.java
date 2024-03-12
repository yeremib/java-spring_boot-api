package com.enigma.wmb_api.service.impl;

import com.enigma.wmb_api.constant.TransactionType;
import com.enigma.wmb_api.dto.request.NewBillDetailRequest;
import com.enigma.wmb_api.dto.request.NewBillRequest;
import com.enigma.wmb_api.dto.request.SearchBillReq;
import com.enigma.wmb_api.dto.request.UpdateTransactionStatusRequest;
import com.enigma.wmb_api.dto.response.BillDetailResponse;
import com.enigma.wmb_api.dto.response.BillResponse;
import com.enigma.wmb_api.dto.response.MenuResponse;
import com.enigma.wmb_api.dto.response.PaymentResponse;
import com.enigma.wmb_api.entity.*;
import com.enigma.wmb_api.repository.BillRepository;
import com.enigma.wmb_api.service.*;
import org.hibernate.Length;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class BillServiceImplTest {

    @Mock
    private BillDetailService billDetailService;
    @Mock
    private UserService userService;
    @Mock
    private TableNumService tableNumService;
    @Mock
    private TransTypeService transTypeService;
    @Mock
    private MenuService menuService;
    @Mock
    private PaymentService paymentService;
    @Mock
    private BillRepository billRepository;

    private BillService billService;

    @BeforeEach
    void setUp() {
        billService = new BillServiceImpl(
                billRepository,
                billDetailService,
                userService,
                tableNumService,
                transTypeService,
                menuService,
                paymentService
        );
    }

    @Test
    void shouldReturnBillWhenCreate() {
        NewBillDetailRequest newBillDetailRequest1 = NewBillDetailRequest.builder()
                .menuId("menuId-1")
                .qty(1)
                .build();

        NewBillDetailRequest newBillDetailRequest2 = NewBillDetailRequest.builder()
                .menuId("menuId-1")
                .qty(1)
                .build();
        List<NewBillDetailRequest> newBillDetailRequests = List.of(newBillDetailRequest1, newBillDetailRequest2);

        NewBillRequest newBillRequest = NewBillRequest.builder()
                .userId("userId-1")
                .tableId("tableId-1")
                .transType("EI")
                .billDetails(newBillDetailRequests)
                .build();

        User user = User.builder().name("yeremi").build();
        TransType transType = TransType.builder().id(TransactionType.EI).description("Eat In").build();
        TableNum tableNum = TableNum.builder().name("T01").build();

        Mockito.when(userService.getOneById(newBillRequest.getUserId())).thenReturn(user);
        Mockito.when(transTypeService.getById(newBillRequest.getTransType())).thenReturn(transType);
        Mockito.when(tableNumService.getById(newBillRequest.getTableId())).thenReturn(tableNum);

        Bill bill = Bill.builder()
                .user(user)
                .tableNum(tableNum)
                .transType(transType)
                .transDate(new Date())
                .build();

        Mockito.when(billRepository.saveAndFlush(Mockito.any())).thenReturn(bill);

        Menu menu = Menu.builder().name("Mie Ayam").build();
        Mockito.when(menuService.getById(newBillDetailRequest1.getMenuId())).thenReturn(menu);

        BillDetail billDetail1 = BillDetail.builder()
                .bill(bill)
                .menu(menu)
                .qty(newBillDetailRequest1.getQty())
                .build();

        Mockito.when(menuService.getById(newBillDetailRequest2.getMenuId())).thenReturn(menu);

        BillDetail billDetail2 = BillDetail.builder()
                .bill(bill)
                .menu(menu)
                .qty(newBillDetailRequest2.getQty())
                .build();

        List<BillDetail> billDetails = List.of(billDetail1, billDetail2);

        Mockito.when(billDetailService.createBulk(Mockito.any())).thenReturn(billDetails);

        bill.setBillDetails(billDetails);

        BillDetailResponse billDetailResponse1 = BillDetailResponse.builder()
                .id(billDetails.get(0).getId())
                .menuId(billDetails.get(0).getMenu().getId())
                .price(billDetails.get(0).getPrice())
                .qty(billDetails.get(0).getQty())
                .build();

        BillDetailResponse billDetailResponse2 = BillDetailResponse.builder()
                .id(billDetails.get(1).getId())
                .menuId(billDetails.get(1).getMenu().getId())
                .price(billDetails.get(1).getPrice())
                .qty(billDetails.get(1).getQty())
                .build();

        Payment payment = Payment.builder().bill(bill).build();

        Mockito.when(paymentService.createPayment(Mockito.any())).thenReturn(payment);
        bill.setPayment(payment);

        PaymentResponse paymentResponse = PaymentResponse.builder()
                .id(payment.getId())
                .token(payment.getToken())
                .redirectUrl(payment.getRedirectUrl())
                .transactionStatus(payment.getTransactionStatus())
                .build();

        BillResponse billResponse = billService.create(newBillRequest);

        assertNotNull(billResponse);

    }

    @Test
    void shouldReturnBillWhenGetById() {
        String id = "billId-1";
        User user = User.builder().name("yeremi").build();
        TransType transType = TransType.builder().id(TransactionType.EI).description("Eat In").build();
        TableNum tableNum = TableNum.builder().name("T01").build();

        Bill bill = Bill.builder()
                .user(user)
                .tableNum(tableNum)
                .transType(transType)
                .transDate(new Date())
                .build();

        Mockito.when(billRepository.findById(id)).thenReturn(Optional.ofNullable(bill));

        Bill bill1 = billService.getBillById(id);

        assertNotNull(bill1);
    }

    @Test
    void shouldReturnBillsWhenGetAll() {
        SearchBillReq billReq = SearchBillReq.builder()
                .page(0)
                .size(5)
                .sortBy("transDate")
                .direction("asc")
                .build();

        User user = User.builder().name("yeremi").build();
        TransType transType = TransType.builder().id(TransactionType.EI).description("Eat In").build();
        TableNum tableNum = TableNum.builder().name("T01").build();

        Bill bill1 = Bill.builder()
                .user(user)
                .tableNum(tableNum)
                .transType(transType)
                .transDate(new Date())
                .build();

        Bill bill2 = Bill.builder()
                .user(user)
                .tableNum(tableNum)
                .transType(transType)
                .transDate(new Date())
                .build();

        Menu menu = Menu.builder().name("Mie Ayam").build();

        BillDetail billDetail1 = BillDetail.builder()
                .bill(bill1)
                .menu(menu)
                .qty(1)
                .build();

        BillDetail billDetail2 = BillDetail.builder()
                .bill(bill1)
                .menu(menu)
                .qty(1)
                .build();

        List<BillDetail> billDetails = List.of(billDetail1, billDetail2);

        bill1.setBillDetails(billDetails);
        bill2.setBillDetails(billDetails);

        Payment payment1 = Payment.builder().bill(bill1).build();
        Payment payment2 = Payment.builder().bill(bill2).build();

        bill1.setPayment(payment1);
        bill2.setPayment(payment2);

        List<Bill> bills = List.of(bill1, bill2);
        Page<Bill> billPage = new PageImpl<>(bills);

        Mockito.when(billRepository.findAll(Mockito.any(Specification.class),Mockito.any(Pageable.class))).thenReturn(billPage);

        Page<BillResponse> billResponses = billService.getAll(billReq);

        assertNotNull(billResponses);
        assertEquals(2, billResponses.getSize());
    }

    @Test
    void getAllBill() {
        User user = User.builder().name("yeremi").build();
        TransType transType = TransType.builder().id(TransactionType.EI).description("Eat In").build();
        TableNum tableNum = TableNum.builder().name("T01").build();

        Bill bill1 = Bill.builder()
                .user(user)
                .tableNum(tableNum)
                .transType(transType)
                .transDate(new Date())
                .build();

        Bill bill2 = Bill.builder()
                .user(user)
                .tableNum(tableNum)
                .transType(transType)
                .transDate(new Date())
                .build();

        Menu menu = Menu.builder().name("Mie Ayam").build();

        BillDetail billDetail1 = BillDetail.builder()
                .bill(bill1)
                .menu(menu)
                .qty(1)
                .build();

        BillDetail billDetail2 = BillDetail.builder()
                .bill(bill1)
                .menu(menu)
                .qty(1)
                .build();

        List<BillDetail> billDetails = List.of(billDetail1, billDetail2);

        bill1.setBillDetails(billDetails);
        bill2.setBillDetails(billDetails);

        Payment payment1 = Payment.builder().bill(bill1).build();
        Payment payment2 = Payment.builder().bill(bill2).build();

        bill1.setPayment(payment1);
        bill2.setPayment(payment2);

        List<Bill> bills = List.of(bill1, bill2);

        Mockito.when(billRepository.findAll()).thenReturn(bills);

        List<Bill> billList = billService.getAllBill();

        assertNotNull(billList);
        assertEquals(2, billList.size());
    }

    @Test
    void updateStatus() {
        UpdateTransactionStatusRequest request = UpdateTransactionStatusRequest.builder()
                .orderId("billId-1")
                .transactionStatus("Settlement")
                .build();

        User user = User.builder().name("yeremi").build();
        TransType transType = TransType.builder().id(TransactionType.EI).description("Eat In").build();
        TableNum tableNum = TableNum.builder().name("T01").build();

        Bill bill = Bill.builder()
                .id("billId-1")
                .user(user)
                .tableNum(tableNum)
                .transType(transType)
                .transDate(new Date())
                .payment(Payment.builder().build())
                .build();

        Mockito.when(billRepository.findById(bill.getId())).thenReturn(Optional.of(bill));

        Payment payment = bill.getPayment();
        payment.setTransactionStatus(request.getTransactionStatus());

        billService.updateStatus(request);

        Mockito.verify(billRepository, Mockito.times(1)).findById(request.getOrderId());

    }
}