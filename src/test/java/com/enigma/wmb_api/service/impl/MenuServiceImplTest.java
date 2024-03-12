package com.enigma.wmb_api.service.impl;

import com.enigma.wmb_api.dto.request.NewMenuRequest;
import com.enigma.wmb_api.dto.request.SearchMenuRequest;
import com.enigma.wmb_api.dto.request.UpdateMenuRequest;
import com.enigma.wmb_api.dto.request.UpdateUserRequest;
import com.enigma.wmb_api.dto.response.MenuResponse;
import com.enigma.wmb_api.entity.Image;
import com.enigma.wmb_api.entity.Menu;
import com.enigma.wmb_api.repository.MenuRepository;
import com.enigma.wmb_api.service.ImageService;
import com.enigma.wmb_api.service.MenuService;
import com.enigma.wmb_api.util.ValidationUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class MenuServiceImplTest {
    @Mock
    private MenuRepository menuRepository;
    @Mock
    private ValidationUtil validationUtil;
    @Mock
    private ImageService imageService;
    private MenuService menuService;

    @BeforeEach
    void setUp() {
        menuService = new MenuServiceImpl(
                menuRepository,
                validationUtil,
                imageService
        );
    }

    @Test
    void shouldReturnMenuWhenCreate() {
        MultipartFile image = new MockMultipartFile("picture", "picture", "image/jpg", "menu picture".getBytes());
        NewMenuRequest request = NewMenuRequest.builder()
                .name("sate")
                .price(12000)
                .image(image)
                .build();

        Mockito.doNothing().when(validationUtil).validate(request);

        Menu mockMenu = Menu.builder()
                .name(request.getName())
                .price(request.getPrice())
                .build();

        if (request.getImage()!= null) {
            Mockito.when(imageService.create(request.getImage())).thenReturn(Image.builder().build());
            mockMenu.setImage(Image.builder().build());
        }

        Mockito.when(menuRepository.saveAndFlush(Mockito.any())).thenReturn(mockMenu);

        MenuResponse menuResponse = menuService.create(request);

        assertNotNull(menuResponse);
        assertEquals("sate", menuResponse.getName());
    }

    @Test
    void shouldReturnMenuWhenGetMenuById() {
        String id = "menuId-1";
        Menu mockMenu = Menu.builder()
                .id("menuId-1")
                .name("sate")
                .price(12000)
                .build();

        Mockito.when(menuRepository.findById(id)).thenReturn(Optional.ofNullable(mockMenu));

        MenuResponse menuResponse = menuService.getMenuById(id);

        assertNotNull(menuResponse);
        assertEquals(id, menuResponse.getId());

    }

    @Test
    void shouldReturnMenuWhenGetById() {
        String id = "menuId-1";
        Menu mockMenu = Menu.builder()
                .id("menuId-1")
                .name("sate")
                .price(12000)
                .build();

        Mockito.when(menuRepository.findById(id)).thenReturn(Optional.ofNullable(mockMenu));

        MenuResponse menuResponse = menuService.getMenuById(id);

        assertNotNull(menuResponse);
        assertEquals(id, menuResponse.getId());
    }

    @Test
    void shouldReturnMenusWhenGetAll() {
        SearchMenuRequest request = SearchMenuRequest.builder()
                .page(0)
                .size(5)
                .sortBy("name")
                .direction("asc")
                .build();

        Menu mockMenu1 = Menu.builder()
                .id("menuId-1")
                .name("sate")
                .price(12000)
                .build();

        Menu mockMenu2 = Menu.builder()
                .id("menuId-2")
                .name("ketoprak")
                .price(12000)
                .build();
        List<Menu> mockMenus = List.of(mockMenu1, mockMenu2);
        Page<Menu> menuPageable = new PageImpl<>(mockMenus);

        Mockito.when(menuRepository.findAll(Mockito.any(Specification.class),Mockito.any(Pageable.class))).thenReturn(menuPageable);

        Page<MenuResponse> menuResponses = menuService.getAll(request);

        assertNotNull(menuResponses);
        assertEquals(2, menuResponses.getSize());
    }

    @Test
    void shouldReturnMenusWhenUpdateMenu() {
        MultipartFile image = new MockMultipartFile("picture", "picture", "image/jpg", "menu picture".getBytes());
        UpdateMenuRequest request = UpdateMenuRequest.builder()
                .id("menuId-1")
                .name("Sate & Lontong")
                .price(13000)
                .imageFile(image)
                .build();

        Menu mockMenu = Menu.builder()
                .id("menuId-1")
                .name("sate")
                .price(12000)
                .image(Image.builder().build())
                .build();

        Mockito.when(menuRepository.findById(request.getId())).thenReturn(Optional.ofNullable(mockMenu));

        Image oldImage = mockMenu.getImage();

        mockMenu.setName(request.getName());
        mockMenu.setPrice(request.getPrice());

        if (request.getImageFile()!=null){
            Mockito.when(imageService.create(request.getImageFile())).thenReturn(Image.builder().build()) ;
            mockMenu.setImage(Image.builder().build());
            if (oldImage != null) {
               Mockito.doNothing().when(imageService).deleteById(oldImage.getId());
            }
        }

        MenuResponse menuResponse = menuService.updateMenu(request);

        assertEquals("Sate & Lontong", menuResponse.getName());
        assertEquals(13000, menuResponse.getPrice());

    }


    @Test
    void delete() {
        String id = "menuId-1";
        Menu mockMenu = Menu.builder()
                .id("menuId-1")
                .name("sate")
                .price(12000)
                .image(Image.builder().build())
                .build();

        Mockito.when(menuRepository.findById(mockMenu.getId())).thenReturn(Optional.of(mockMenu));
        Mockito.doNothing().when(menuRepository).delete(mockMenu);
        Mockito.doNothing().when(imageService).deleteById(mockMenu.getImage().getId());

        menuService.delete(id);

        Mockito.verify(menuRepository, Mockito.times(1)).delete(mockMenu);

    }
}