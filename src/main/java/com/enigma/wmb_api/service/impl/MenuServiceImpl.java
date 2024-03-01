package com.enigma.wmb_api.service.impl;

import com.enigma.wmb_api.dto.request.NewMenuRequest;
import com.enigma.wmb_api.dto.request.SearchMenuRequest;
import com.enigma.wmb_api.entity.Menu;
import com.enigma.wmb_api.repository.MenuRepository;
import com.enigma.wmb_api.service.MenuService;
import com.enigma.wmb_api.specification.MenuSpecification;
import com.enigma.wmb_api.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final MenuRepository menuRepository;
    private final ValidationUtil validationUtil;

    @Override
    public Menu create(NewMenuRequest request) {
        validationUtil.validate(request);
        Menu menu = Menu.builder()
                .name(request.getName())
                .price(request.getPrice())
                .build();
        return menuRepository.saveAndFlush(menu);
    }

    @Override
    public Menu getById(String id) {
        Optional<Menu> menu = menuRepository.findById(id);
        return menu.get();
    }

    @Override
    public Page<Menu> getAll(SearchMenuRequest request) {
        if (request.getPage() <= 0) request.setPage(1);
        Sort sort = Sort.by(Sort.Direction.fromString(request.getDirection()), request.getSortBy());
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize(), sort);
        Specification<Menu> specification  = MenuSpecification.getSpesification(request);
        return menuRepository.findAll(specification, pageable);
    }

    @Override
    public Menu update(Menu menu) {
        getById(menu.getId());
        return menuRepository.saveAndFlush(menu);
    }

    @Override
    public void delete(String id) {
        Menu menu = getById(id);
        menuRepository.delete(menu);
    }
}
