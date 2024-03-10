package com.enigma.wmb_api.controller;

import com.enigma.wmb_api.constant.APIUrl;
import com.enigma.wmb_api.dto.request.NewTableNumReq;
import com.enigma.wmb_api.dto.response.CommonResponse;
import com.enigma.wmb_api.entity.TableNum;
import com.enigma.wmb_api.service.TableNumService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = APIUrl.TABLE_NUM_API)
public class TableNumController {
    private final TableNumService tableNumService;
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @PostMapping
    public ResponseEntity<CommonResponse<TableNum>> createNewTableNum(@RequestBody NewTableNumReq req) {
        TableNum tableNum = tableNumService.create(req);
        CommonResponse<TableNum> response = CommonResponse.<TableNum>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("successfully create new table number")
                .data(tableNum)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<CommonResponse<TableNum>> getTableNumById(@PathVariable String id) {
        TableNum tableNum = tableNumService.getById(id);
        CommonResponse<TableNum> response = CommonResponse.<TableNum>builder()
                .statusCode(HttpStatus.OK.value())
                .message("table number found")
                .data(tableNum)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<CommonResponse<List<TableNum>>> getAllTableNum(
            @RequestParam(name = "name", required = false) String name
    ) {
        List<TableNum> allTableNum = tableNumService.getAll(name);
        CommonResponse<List<TableNum>> response = CommonResponse.<List<TableNum>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("successfully get all table number")
                .data(allTableNum)
                .build();
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @PutMapping
    public ResponseEntity<CommonResponse<TableNum>> updateTableNum(@RequestBody TableNum tableNum) {
        TableNum tableNum1 = tableNumService.update(tableNum);
        CommonResponse<TableNum> response = CommonResponse.<TableNum>builder()
                .statusCode(HttpStatus.OK.value())
                .message("successfully update table number")
                .data(tableNum1)
                .build();
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<CommonResponse<TableNum>> deleteTableNumById(@PathVariable String id) {
        TableNum tableNum = tableNumService.getById(id);
        tableNumService.delete(id);
        CommonResponse<TableNum> response = CommonResponse.<TableNum>builder()
                .statusCode(HttpStatus.OK.value())
                .message("successfully delete table number")
                .data(tableNum)
                .build();
        return ResponseEntity.ok(response);
    }
}
