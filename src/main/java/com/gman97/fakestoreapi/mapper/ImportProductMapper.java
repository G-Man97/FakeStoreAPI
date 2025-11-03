package com.gman97.fakestoreapi.mapper;

import com.gman97.fakestoreapi.dto.ProductDto;
import com.gman97.fakestoreapi.entity.Category;
import com.gman97.fakestoreapi.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ImportProductMapper implements Mapper<ProductDto, Product> {

    private final RatingReadMapper ratingReadMapper;

    @Override
    public Product map(ProductDto obj) {
        return Product.builder()
                .externalId(obj.getId())
                .title(obj.getTitle())
                .price(obj.getPrice())
                .description(obj.getDescription())
                .category(new Category(null, obj.getCategory()))
                .image(obj.getImage())
                .rating(ratingReadMapper.map(obj.getRating()))
                .build();
    }
}