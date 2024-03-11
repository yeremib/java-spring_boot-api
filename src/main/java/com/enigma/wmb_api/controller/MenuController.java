package com.enigma.wmb_api.controller;

import com.enigma.wmb_api.constant.APIUrl;
import com.enigma.wmb_api.dto.request.NewMenuRequest;
import com.enigma.wmb_api.dto.request.SearchMenuRequest;
import com.enigma.wmb_api.dto.request.UpdateMenuRequest;
import com.enigma.wmb_api.dto.response.CommonResponse;
import com.enigma.wmb_api.dto.response.MenuResponse;
import com.enigma.wmb_api.dto.response.PagingResponse;
import com.enigma.wmb_api.entity.Menu;
import com.enigma.wmb_api.service.MenuService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = APIUrl.MENU_API)
public class MenuController {

    private final MenuService menuService;
    private final ObjectMapper objectMapper;

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CommonResponse<MenuResponse>> createNewMenu(
            @RequestPart (name = "menu") String jsonProduct,
            @RequestParam (name = "image", required = false) MultipartFile image
    ) {
        CommonResponse.CommonResponseBuilder<MenuResponse> responseBuilder = CommonResponse.builder();

        try {
            NewMenuRequest request = objectMapper.readValue(jsonProduct, new TypeReference<>() {});
            request.setImage(image);

            MenuResponse menuResponse = menuService.create(request);
            responseBuilder.statusCode(HttpStatus.CREATED.value());
            responseBuilder.message("successfully create new menu");
            responseBuilder.data(menuResponse);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseBuilder.build());
        } catch (Exception e) {
            e.printStackTrace();
            responseBuilder.statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseBuilder.message("internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBuilder.build());
        }
    }

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse<MenuResponse>> getMenuById(@PathVariable String id) {
        MenuResponse menu = menuService.getMenuById(id);
        CommonResponse<MenuResponse> response = CommonResponse.<MenuResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("successfully get menu")
                .data(menu)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<CommonResponse<List<MenuResponse>>> getAllMenu(
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "size", defaultValue = "5") Integer size,
            @RequestParam(name = "sortBy", defaultValue = "name") String sortBy,
            @RequestParam(name = "direction", defaultValue = "asc") String direction,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "minPrice", required = false) Integer minPrice,
            @RequestParam(name = "maxPrice", required = false) Integer maxPrice
    ) {
        SearchMenuRequest request = SearchMenuRequest.builder()
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .direction(direction)
                .name(name)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .build();

        Page<MenuResponse> menus = menuService.getAll(request);

        PagingResponse pagingResponse = PagingResponse.builder()
                .totalPages(menus.getTotalPages())
                .totalElement(menus.getTotalElements())
                .page(menus.getPageable().getPageNumber() + 1)
                .size(menus.getPageable().getPageSize())
                .hasNext(menus.hasNext())
                .hasPrevious(menus.hasPrevious())
                .build();

        CommonResponse<List<MenuResponse>> response = CommonResponse.<List<MenuResponse>> builder()
                .statusCode(HttpStatus.OK.value())
                .message("successfully get all menu")
                .data(menus.getContent())
                .paging(pagingResponse)
                .build();

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @PutMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CommonResponse<MenuResponse>> updateMenu(
            @RequestParam (name = "menu") String jsonProduct,
            @RequestParam (name = "image", required = false) MultipartFile image
    ) throws JsonProcessingException {
        CommonResponse.CommonResponseBuilder<MenuResponse> responseBuilder = CommonResponse.builder();

        UpdateMenuRequest request = objectMapper.readValue(jsonProduct, new TypeReference<>() {});
        request.setImageFile(image);
        MenuResponse menuResponse = menuService.updateMenu(request);
        responseBuilder.statusCode(HttpStatus.OK.value());
        responseBuilder.message("successfully update menu");
        responseBuilder.data(menuResponse);
        return ResponseEntity.ok(responseBuilder.build());
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @DeleteMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse<MenuResponse>> deleteMenuById(@PathVariable String id) {
        MenuResponse menu = menuService.getMenuById(id);
        menuService.delete(id);
        CommonResponse<MenuResponse> response = CommonResponse.<MenuResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Menu deleted")
                .data(menu)
                .build();
        return ResponseEntity.ok(response);
    }

}
