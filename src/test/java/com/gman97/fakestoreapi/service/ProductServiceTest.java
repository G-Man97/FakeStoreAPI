package com.gman97.fakestoreapi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gman97.fakestoreapi.dto.ProductDto;
import com.gman97.fakestoreapi.dto.ProductFilter;
import com.gman97.fakestoreapi.dto.RatingReadDto;
import com.gman97.fakestoreapi.entity.Category;
import com.gman97.fakestoreapi.entity.Product;
import com.gman97.fakestoreapi.entity.RateCount;
import com.gman97.fakestoreapi.entity.Rating;
import com.gman97.fakestoreapi.mapper.ImportProductMapper;
import com.gman97.fakestoreapi.mapper.ProductCreateEditMapper;
import com.gman97.fakestoreapi.mapper.ProductReadMapper;
import com.gman97.fakestoreapi.mapper.RatingReadMapper;
import com.gman97.fakestoreapi.repository.CategoryRepository;
import com.gman97.fakestoreapi.repository.ProductRepository;
import com.gman97.fakestoreapi.repository.RatingRepository;
import com.querydsl.core.types.Predicate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private RatingRepository ratingRepository;
    @Mock
    private ImportProductMapper importProductMapper;
    @Mock
    private ProductReadMapper productReadMapper;
    @Mock
    private RatingReadMapper ratingReadMapper;
    @Mock
    private ProductCreateEditMapper productCreateEditMapper;

    private Product product1 = new Product();
    private Product product2 = new Product();

    @BeforeEach
    void setUp() {
        product1 = Product.builder()
                .id(1)
                .externalId(10)
                .title("dummy")
                .price(1.0D)
                .description("dummy")
                .category(new Category(1, "dummy"))
                .image("https://dummy.foo/img.jpg")
                .rating(new Rating(1L, new RateCount(3.0D, 359)))
                .build();
        product2 = Product.builder()
                .id(null)
                .externalId(12)
                .title("dummy2")
                .price(2.0D)
                .description("dummy2")
                .category(new Category(2, "dummy2"))
                .image("https://dummy.foo/img2.jpg")
                .rating(new Rating(null, new RateCount(5.0D, 159)))
                .build();
    }

    @Test
    void findAllByFilter() {
        ProductFilter emptyFilter = new ProductFilter();
        Integer page = 0;
        Integer size = 1;
        ProductDto foundProduct = new ProductDto();

        when(productRepository.findAll(any(Predicate.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(new Product())));

        when(productReadMapper.map(any(Product.class))).thenReturn(foundProduct);

        var result = productService.findAllByFilter(emptyFilter, page, size);

        assertEquals(new PageImpl<>(List.of(foundProduct)), result);

        verify(productRepository).findAll(any(Predicate.class), any(Pageable.class));
        verify(productReadMapper).map(any(Product.class));

    }

    @Test
    void saveImportedProducts() throws JsonProcessingException {
        var dummyDtos = List.of(new ProductDto(), new ProductDto());

        when(objectMapper.readValue(anyString(), any(TypeReference.class))).thenReturn(dummyDtos);
        when(importProductMapper.map(any(ProductDto.class))).thenReturn(product1).thenReturn(product2);

        when(categoryRepository.findAllByNames(anyList())).thenReturn(List.of(product1.getCategory()));
        when(categoryRepository.save(any(Category.class))).thenReturn(product2.getCategory());
        doNothing().when(categoryRepository).flush();

        when(ratingRepository.findAllByRateAndCount(anyList())).thenReturn(List.of(product1.getRating()));
        when(ratingRepository.save(any(Rating.class))).thenReturn(product2.getRating());
        doNothing().when(ratingRepository).flush();

        when(productRepository.findAllByExternalIds(anyList())).thenReturn(List.of(product1));
        when(productRepository.saveAll(anyList())).thenReturn(List.of(product1, product2));


        productService.saveImportedProducts();


        verify(importProductMapper, times(dummyDtos.size())).map(any(ProductDto.class));

        verify(categoryRepository).findAllByNames(anyList());
        verify(categoryRepository).save(any(Category.class));
        verify(categoryRepository).flush();

        verify(ratingRepository).findAllByRateAndCount(anyList());
        verify(ratingRepository).save(any(Rating.class));
        verify(ratingRepository).flush();

        verify(productRepository).findAllByExternalIds(anyList());
        verify(productRepository).saveAll(anyList());
    }

    @Test
    void save() {
        var dummyDto = ProductDto.builder()
                .category(product1.getCategory().getName())
                .rating(new RatingReadDto(2.3D, 100))
                .build();

        when(categoryRepository.findByName(anyString())).thenReturn(Optional.of(product1.getCategory()));
        when(ratingReadMapper.map(any(RatingReadDto.class))).thenReturn(product1.getRating());
        when(ratingRepository.findByRateAndCount(any(RateCount.class))).thenReturn(Optional.of(product1.getRating()));

        when(productCreateEditMapper.map(dummyDto)).thenReturn(product1);
        when(productRepository.saveAndFlush(product1)).thenReturn(product1);
        when(productReadMapper.map(product1)).thenReturn(dummyDto);

        var result = productService.save(dummyDto);

        assertEquals(dummyDto, result);

        verify(categoryRepository).findByName(anyString());
        verify(categoryRepository, never()).saveAndFlush(any(Category.class));

        verify(ratingReadMapper).map(any(RatingReadDto.class));
        verify(ratingRepository).findByRateAndCount(any(RateCount.class));
        verify(ratingRepository, never()).saveAndFlush(any(Rating.class));

        verify(productCreateEditMapper).map(dummyDto);
        verify(productRepository).saveAndFlush(product1);
        verify(productReadMapper).map(product1);
    }

    @Test
    void saveWhenCategoryAndRatingNotFoundThenFirstOfAllSaveThem() {
        var dummyDto = ProductDto.builder()
                .category("new category")
                .rating(new RatingReadDto(2.2D, 222))
                .build();

        when(categoryRepository.findByName(anyString())).thenReturn(Optional.empty());
        when(categoryRepository.saveAndFlush(any(Category.class))).thenReturn(product1.getCategory());

        when(ratingReadMapper.map(any(RatingReadDto.class))).thenReturn(product1.getRating());
        when(ratingRepository.findByRateAndCount(any(RateCount.class))).thenReturn(Optional.empty());
        when(ratingRepository.saveAndFlush(any(Rating.class))).thenReturn(product1.getRating());

        when(productCreateEditMapper.map(dummyDto)).thenReturn(product1);
        when(productRepository.saveAndFlush(product1)).thenReturn(product1);
        when(productReadMapper.map(product1)).thenReturn(dummyDto);

        var result = productService.save(dummyDto);

        assertEquals(dummyDto, result);

        verify(categoryRepository).findByName(anyString());
        verify(categoryRepository).saveAndFlush(any(Category.class));

        verify(ratingReadMapper, times(2)).map(any(RatingReadDto.class));
        verify(ratingRepository).findByRateAndCount(any(RateCount.class));
        verify(ratingRepository).saveAndFlush(any(Rating.class));

        verify(productCreateEditMapper).map(dummyDto);
        verify(productRepository).saveAndFlush(product1);
        verify(productReadMapper).map(product1);
    }

    @Test
    void update() {
        var dummyDto = ProductDto.builder()
                .category(product1.getCategory().getName())
                .rating(new RatingReadDto(2.3D, 100))
                .build();

        when(productRepository.findById(anyInt())).thenReturn(Optional.of(product1));

        when(categoryRepository.findByName(anyString())).thenReturn(Optional.of(product1.getCategory()));
        when(ratingReadMapper.map(any(RatingReadDto.class))).thenReturn(product1.getRating());
        when(ratingRepository.findByRateAndCount(any(RateCount.class))).thenReturn(Optional.of(product1.getRating()));

        when(productCreateEditMapper.map(any(ProductDto.class), any(Product.class))).thenReturn(product1);
        when(productRepository.saveAndFlush(product1)).thenReturn(product1);
        when(productReadMapper.map(product1)).thenReturn(dummyDto);


        var result = productService.update(product1.getId(), dummyDto);

        assertEquals(Optional.of(dummyDto), result);

        verify(productRepository).findById(anyInt());
        verify(categoryRepository).findByName(anyString());
        verify(categoryRepository, never()).saveAndFlush(any(Category.class));

        verify(ratingReadMapper).map(any(RatingReadDto.class));
        verify(ratingRepository).findByRateAndCount(any(RateCount.class));
        verify(ratingRepository, never()).saveAndFlush(any(Rating.class));

        verify(productCreateEditMapper).map(any(ProductDto.class), any(Product.class));
        verify(productRepository).saveAndFlush(product1);
        verify(productReadMapper).map(product1);
    }

    @Test
    void deleteReturnTrueWhenProductExistsInDB() {
        var productId = 1;

        when(productRepository.findById(productId)).thenReturn(Optional.of(product1));
        doNothing().when(productRepository).delete(product1);
        doNothing().when(productRepository).flush();

        var result = productService.delete(productId);

        assertTrue(result);

        verify(productRepository).findById(productId);
        verify(productRepository).delete(product1);
        verify(productRepository).flush();
    }

    @Test
    void deleteReturnFalseWhenProductDoesNotExistsInDB() {
        var productId = 2;

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        var result = productService.delete(productId);

        assertFalse(result);

        verify(productRepository).findById(productId);
        verify(productRepository, never()).delete(product1);
        verify(productRepository, never()).flush();
    }
}