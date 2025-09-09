package com.gman97.fakestoreapi.http.rest;

import com.gman97.fakestoreapi.dto.PageResponse;
import com.gman97.fakestoreapi.dto.ProductDto;
import com.gman97.fakestoreapi.dto.ProductFilter;
import com.gman97.fakestoreapi.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public PageResponse<ProductDto> findAll(ProductFilter filter,
                                            @RequestParam(required = false, defaultValue = "0") Integer page,
                                            @RequestParam(required = false, defaultValue = "5") Integer size) {
        Page<ProductDto> pageForResponse = productService.findAllByFilter(filter, page, size);
        return PageResponse.of(pageForResponse);
    }

    @GetMapping("/{id}")
    public ProductDto findById(@PathVariable Integer id) {
        return productService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/by-category/{categoryName}")
    public PageResponse<ProductDto> findAllByCategoryName(ProductFilter filter,
                                                          @RequestParam(required = false, defaultValue = "0") Integer page,
                                                          @RequestParam(required = false, defaultValue = "5") Integer size) {
        Page<ProductDto> pageForResponse = productService.findAllByFilter(filter, page, size);
        return PageResponse.of(pageForResponse);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDto create(@RequestBody ProductDto productDto) {
        return productService.save(productDto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ProductDto update(@PathVariable Integer id, @RequestBody ProductDto productDto) {
        return productService.update(id, productDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        return productService.delete(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
