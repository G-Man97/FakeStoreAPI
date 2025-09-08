package com.gman97.fakestoreapi.http.rest;

import com.gman97.fakestoreapi.dto.PageResponse;
import com.gman97.fakestoreapi.dto.ProductDto;
import com.gman97.fakestoreapi.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public PageResponse<ProductDto> findAll(@RequestParam(required = false) Double minPrice,
                                            @RequestParam(required = false) Double maxPrice,
                                            @RequestParam(required = false, defaultValue = "0") Integer page,
                                            @RequestParam(required = false, defaultValue = "5") Integer size) {
        Page<ProductDto> pageForResponse = productService.findAll(minPrice, maxPrice, page, size);
        return PageResponse.of(pageForResponse);
    }

    @GetMapping("/{id}")
    public ProductDto findById(@PathVariable Integer id) {
        return productService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/by-category/{categoryName}")
    public PageResponse<ProductDto> findAllByCategoryName(@PathVariable String categoryName,
                                                          @RequestParam(required = false, defaultValue = "0") Integer page,
                                                          @RequestParam(required = false, defaultValue = "5") Integer size) {
        Page<ProductDto> pageForResponse = productService.findAllByCategoryName(categoryName, page, size);
        return PageResponse.of(pageForResponse);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDto create(@RequestBody ProductDto productDto) {
        return productService.save(productDto);
    }

    @PutMapping("/{id}")
    public ProductDto update(@PathVariable Integer id, @RequestBody ProductDto productDto) {
        return productService.update(id, productDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (productService.delete(id)) {
            return ResponseEntity.noContent().build();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}
