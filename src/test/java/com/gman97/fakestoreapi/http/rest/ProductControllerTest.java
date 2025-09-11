package com.gman97.fakestoreapi.http.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gman97.fakestoreapi.config.PasswordEncoderConfiguration;
import com.gman97.fakestoreapi.config.SecurityConfiguration;
import com.gman97.fakestoreapi.dto.PageResponse;
import com.gman97.fakestoreapi.dto.ProductDto;
import com.gman97.fakestoreapi.dto.ProductFilter;
import com.gman97.fakestoreapi.dto.RatingReadDto;
import com.gman97.fakestoreapi.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@Import({SecurityConfiguration.class, PasswordEncoderConfiguration.class})
@WithMockUser(username = "admin@gmail.com", password = "test12345678", authorities = {"ADMIN"})
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    private ProductDto productDto1 = new ProductDto();
    private ProductDto productDto2 = new ProductDto();

    @BeforeEach
    void setUp() {
        productDto1 = ProductDto.builder()
                .id(1)
                .title("dummy")
                .price(1.0D)
                .description("dummy")
                .category("dummy")
                .image("https://dummy.foo/img.jpg")
                .rating(new RatingReadDto(3.0D, 359))
                .build();
        productDto2 = ProductDto.builder()
                .id(2)
                .title("dummy2")
                .price(2.0D)
                .description("dummy2")
                .category("dummy2")
                .image("https://dummy.foo/img2.jpg")
                .rating(new RatingReadDto(5.0D, 159))
                .build();
    }

    @Test
    void findAll() throws Exception {
        var dtoList = List.of(productDto1, productDto2);
        var result = new PageImpl<>(dtoList);

        when(productService.findAllByFilter(any(ProductFilter.class), anyInt(), anyInt())).thenReturn(result);

        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(PageResponse.of(result))));

        verify(productService).findAllByFilter(any(ProductFilter.class), anyInt(), anyInt());
    }

    @Test
    void findById() throws Exception {
        when(productService.findById(anyInt())).thenReturn(Optional.of(productDto1));

        mockMvc.perform(get("/api/v1/products/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(productDto1)));

        verify(productService).findById(anyInt());
    }

    @Test
    void findByIdWhenProductNotFoundInDBThenResponseStatusIs404() throws Exception {
        when(productService.findById(anyInt())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/products/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));

        verify(productService).findById(anyInt());
    }

    @Test
    void create() throws Exception {
        when(productService.save(any(ProductDto.class))).thenReturn(productDto1);

        mockMvc.perform(post("/api/v1/products")
                .content(objectMapper.writeValueAsString(productDto1)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(productDto1)));

        verify(productService).save(any(ProductDto.class));
    }

    @Test
    void createWhenDtoIsInvalidThenResponseStatus400() throws Exception {
        productDto1.setPrice(100000D);

        mockMvc.perform(post("/api/v1/products")
                        .content(objectMapper.writeValueAsString(productDto1)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper
                        .writeValueAsString(List.of("Поле price должно содержать число до 9999.99"))));

        verify(productService, never()).save(any(ProductDto.class));
    }

    @Test
    void update() throws Exception {
        when(productService.update(anyInt(), any(ProductDto.class))).thenReturn(Optional.of(productDto1));

        mockMvc.perform(put("/api/v1/products/1")
                .content(objectMapper.writeValueAsString(productDto1)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(productDto1)));

        verify(productService).update(anyInt(), any(ProductDto.class));
    }

    @Test
    void updateWhenDtoIsInvalidThenResponseStatus400() throws Exception {
        productDto1.setTitle("!");

        mockMvc.perform(put("/api/v1/products/1")
                        .content(objectMapper.writeValueAsString(productDto1)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper
                        .writeValueAsString(List.of("Поле title должно содержать от 2 до 128 символов"))));

        verify(productService, never()).update(anyInt(), any(ProductDto.class));
    }

    @Test
    void updateWhenProductNotFoundInDBThenResponseStatus404() throws Exception {
        when(productService.update(anyInt(), any(ProductDto.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/v1/products/1")
                        .content(objectMapper.writeValueAsString(productDto1)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));

        verify(productService).update(anyInt(), any(ProductDto.class));
    }

    @Test
    void delete() throws Exception {
        when(productService.delete(anyInt())).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/products/1"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(productService).delete(anyInt());
    }

    @Test
    void deleteWhenProductNotFoundInDBThenResponseStatus404() throws Exception {
        when(productService.delete(anyInt())).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/products/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));

        verify(productService).delete(anyInt());
    }
}