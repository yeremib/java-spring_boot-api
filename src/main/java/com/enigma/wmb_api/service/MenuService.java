package com.enigma.wmb_api.service;

import com.enigma.wmb_api.dto.request.NewMenuRequest;
import com.enigma.wmb_api.dto.request.SearchMenuRequest;
import com.enigma.wmb_api.entity.Menu;
import org.springframework.data.domain.Page;


public interface MenuService {
    Menu create(NewMenuRequest request);
    Menu getById(String id);
    Page<Menu> getAll(SearchMenuRequest request);
    Menu update(Menu menu);
    void delete(String id);
}
