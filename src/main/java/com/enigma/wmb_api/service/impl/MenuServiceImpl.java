package com.enigma.wmb_api.service.impl;

import com.enigma.wmb_api.constant.APIUrl;
import com.enigma.wmb_api.dto.request.NewMenuRequest;
import com.enigma.wmb_api.dto.request.SearchMenuRequest;
import com.enigma.wmb_api.dto.request.UpdateMenuRequest;
import com.enigma.wmb_api.dto.response.ImageResponse;
import com.enigma.wmb_api.dto.response.MenuResponse;
import com.enigma.wmb_api.entity.Image;
import com.enigma.wmb_api.entity.Menu;
import com.enigma.wmb_api.repository.MenuRepository;
import com.enigma.wmb_api.service.ImageService;
import com.enigma.wmb_api.service.MenuService;
import com.enigma.wmb_api.specification.MenuSpecification;
import com.enigma.wmb_api.util.ValidationUtil;
import jakarta.validation.ConstraintViolationException;
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

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final MenuRepository menuRepository;
    private final ValidationUtil validationUtil;
    private final ImageService imageService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public MenuResponse create(NewMenuRequest request) {
        validationUtil.validate(request);
        if (request.getImage().isEmpty()) throw new ConstraintViolationException("image is required", null);
        Image image = imageService.create(request.getImage());
        Menu menu = Menu.builder()
                .name(request.getName())
                .price(request.getPrice())
                .image(image)
                .build();

        menuRepository.saveAndFlush(menu);
        return convertMenuToMenuResponse(menu);
    }

    @Transactional(readOnly = true)
    @Override
    public MenuResponse getMenuById(String id) {
        Menu menu = findByIdOrThrowNotFound(id);
        return convertMenuToMenuResponse(menu);
    }

    @Transactional(readOnly = true)
    @Override
    public Menu getById(String id) {
        Optional<Menu> menu = menuRepository.findById(id);
        if(menu.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "menu not found");
        return menu.get();
    }

    @Transactional(readOnly = true)
    @Override
    public Page<MenuResponse> getAll(SearchMenuRequest request) {
        if (request.getPage() <= 0) request.setPage(1);
        Sort sort = Sort.by(Sort.Direction.fromString(request.getDirection()), request.getSortBy());
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize(), sort);
        Specification<Menu> specification  = MenuSpecification.getSpesification(request);
        return menuRepository.findAll(specification, pageable).map(this::convertMenuToMenuResponse);
    }

    @Override
    public MenuResponse updateMenu(UpdateMenuRequest request) {
        Menu currentMenu = findByIdOrThrowNotFound(request.getId());
        currentMenu.setName(request.getName());
        currentMenu.setPrice(request.getPrice());

        if (request.getImageFile() == null) {
            currentMenu.setImage(currentMenu.getImage());
        } else {
            String imageId = currentMenu.getImage().getId();
            Image image = imageService.create(request.getImageFile());
            currentMenu.setImage(image);
            imageService.deleteById(imageId);
        }

        menuRepository.saveAndFlush(currentMenu);
        return convertMenuToMenuResponse(currentMenu);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Menu update(Menu menu) {
        getById(menu.getId());
        return menuRepository.saveAndFlush(menu);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(String id) {
        Menu menu = getById(id);
        menuRepository.delete(menu);
        imageService.deleteById(menu.getImage().getId());
    }

    private Menu findByIdOrThrowNotFound(String id) {
        return menuRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "menu not found"));
    }

    private MenuResponse convertMenuToMenuResponse(Menu menu) {
        return MenuResponse.builder()
                .id(menu.getId())
                .name(menu.getName())
                .price(menu.getPrice())
                .image(ImageResponse.builder()
                        .url(APIUrl.PRODUCT_IMAGE_DOWNLOAD_API + menu.getImage().getId())
                        .name(menu.getImage().getName())
                        .build())
                .build();
    }

}
