package com.enigma.wmb_api.service;

import com.enigma.wmb_api.dto.request.NewMenuRequest;
import com.enigma.wmb_api.dto.request.SearchMenuRequest;
import com.enigma.wmb_api.dto.request.UpdateMenuRequest;
import com.enigma.wmb_api.dto.response.MenuResponse;
import com.enigma.wmb_api.entity.Menu;
import org.springframework.data.domain.Page;


public interface MenuService {
    MenuResponse create(NewMenuRequest request);
    MenuResponse getMenuById(String id);
    Menu getById(String id);
    Page<MenuResponse> getAll(SearchMenuRequest request);
    MenuResponse updateMenu(UpdateMenuRequest request);
    void delete(String id);
}
