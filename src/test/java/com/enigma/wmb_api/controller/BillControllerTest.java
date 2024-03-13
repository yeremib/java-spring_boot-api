package com.enigma.wmb_api.controller;

import com.enigma.wmb_api.constant.APIUrl;
import com.enigma.wmb_api.constant.TransactionType;
import com.enigma.wmb_api.dto.request.NewBillRequest;
import com.enigma.wmb_api.dto.request.SearchBillReq;
import com.enigma.wmb_api.dto.response.BillResponse;
import com.enigma.wmb_api.dto.response.CommonResponse;
import com.enigma.wmb_api.entity.*;
import com.enigma.wmb_api.service.BillService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class BillControllerTest {

    @MockBean
    private BillService billService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    @Test
    @WithMockUser(username = "yeremi", roles = {"ADMIN"})
    void should201StatusAndReturnCommonResponsWhenCreateBill() throws Exception {
        NewBillRequest request = NewBillRequest.builder()
                .userId("C001")
                .tableId("T001")
                .transType("EI")
                .build();

        BillResponse response = BillResponse.builder()
                .id("B001")
                .userId(request.getUserId())
                .tableId(request.getTableId())
                .transType(TransactionType.EI)
                .transDate(new Date())
                .build();

        Mockito.when(billService.create(request)).thenReturn(response);
        String jsonBill = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                MockMvcRequestBuilders.post(APIUrl.BILL_API)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(jsonBill)
        ).andExpect(MockMvcResultMatchers.status().isCreated())
                .andDo(print())
                .andDo(result -> {
                    CommonResponse<BillResponse> resp = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
                    assertNotNull(resp);
                    assertEquals(resp.getStatusCode(), 201);
//                    assertEquals(resp.getMessage(), "Successfully create new bill");
                });
    }

    @Test
    @WithMockUser(username = "yeremi", roles = {"ADMIN"})
    void should200StatusAndReturnCommonResponseWhenGetById() throws Exception {
        String id = "billId-1";

        User user = User.builder().id("userId-1").build();
        TableNum tableNum = TableNum.builder().id("tableId-1").build();
        TransType transType = TransType.builder().id(TransactionType.EI).build();

        Bill bill = Bill.builder()
                .id("billId-1")
                .transDate(new Date())
                .user(user)
                .tableNum(tableNum)
                .transType(transType)
                .build();

        BillDetail billDetail = BillDetail.builder().id("billDetailId-1").build();
        List<BillDetail> billDetails = List.of(billDetail);

        bill.setBillDetails(billDetails);

        Mockito.when(billService.getBillById(id)).thenReturn(bill);


        mockMvc.perform(
                MockMvcRequestBuilders.get(APIUrl.BILL_API+"/"+bill.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print())
                .andDo(result -> {
                    CommonResponse<Bill> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {});
                    assertNotNull(response);
                    assertEquals(response.getStatusCode(), 200);
                });

    }

    @Test
    @WithMockUser(username = "yeremi", roles = {"ADMIN"})
    void getAllBill() throws Exception {
        SearchBillReq req = SearchBillReq.builder()
                .page(0)
                .size(5)
                .sortBy("transType")
                .direction("asc")
                .build();

        BillDetail billDetail = BillDetail.builder().id("billDetail-1").build();
        List<BillDetail> details = List.of(billDetail);

        BillResponse response = BillResponse.builder()
                .id("billId-1")
                .userId("userId-1")
                .transDate(new Date())
                .tableId("tableId-1")
                .transType(TransactionType.EI)
                .build();

        Page<BillResponse> billResponsePage = Mockito.mock(Page.class);

        Mockito.when(billService.getAll(Mockito.any())).thenReturn(billResponsePage);
        Mockito.when(billResponsePage.getTotalPages()).thenReturn(2);
        Mockito.when(billResponsePage.getPageable()).thenReturn(PageRequest.of(1,2));

        mockMvc.perform(
                MockMvcRequestBuilders.get(APIUrl.BILL_API)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print())
                .andDo(result -> {
                    CommonResponse<List<BillResponse>> response1 = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertEquals(response1.getStatusCode(), 200);
                });
    }

    @Test
    void updateStatus() {
    }

    @Test
    void generateCsvFile() {
    }

    @Test
    void exportToPdf() {
    }
}