package com.gman97.fakestoreapi.mapper;

import com.gman97.fakestoreapi.dto.ProductDto;
import com.gman97.fakestoreapi.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductReadMapper implements Mapper<Product, ProductDto>{

   private final RatingReadMapper ratingReadMapper;

    @Override
    public ProductDto map(Product obj) {
        return ProductDto.builder()
                .id(obj.getId())
                .title(obj.getTitle())
                .price(obj.getPrice())
                .description(obj.getDescription())
                .category(obj.getCategory().getName())
                .image(obj.getImage())
                .rating(ratingReadMapper.mapToDto(obj.getRating()))
                .build();
    }
}
