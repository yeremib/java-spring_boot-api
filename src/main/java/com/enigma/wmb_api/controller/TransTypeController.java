package com.enigma.wmb_api.controller;

import com.enigma.wmb_api.constant.APIUrl;
import com.enigma.wmb_api.dto.response.CommonResponse;
import com.enigma.wmb_api.entity.TransType;
import com.enigma.wmb_api.service.TransTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = APIUrl.TRANS_TYPE_API)
public class TransTypeController {
    private final TransTypeService transTypeService;

    @GetMapping(path = "/{id}")
    public ResponseEntity<CommonResponse<TransType>> getTransTypeById(@PathVariable String id) {
        TransType transType = transTypeService.getById(id);
        CommonResponse<TransType> response = CommonResponse.<TransType>builder()
                .statusCode(HttpStatus.OK.value())
                .message("transaction type found")
                .data(transType)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<CommonResponse<List<TransType>>> getAllTransType(
            @RequestParam(name = "description", required = false) String description
    ){
        List<TransType> allTransType = transTypeService.getAll();
        CommonResponse<List<TransType>> response = CommonResponse.<List<TransType>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("successfully get all transaction type")
                .data(allTransType)
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<CommonResponse<TransType>> updateTransType(@RequestBody TransType transType){
        TransType transType1 = transTypeService.getById(transType.getId().name());
        CommonResponse<TransType> response = CommonResponse.<TransType>builder()
                .statusCode(HttpStatus.OK.value())
                .message("successfully update transaction type")
                .data(transType1)
                .build();

        return ResponseEntity.ok(response);
    }
}
