package com.gman97.fakestoreapi.mapper;

import com.gman97.fakestoreapi.dto.ImportProductDto;
import com.gman97.fakestoreapi.entity.Category;
import com.gman97.fakestoreapi.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ImportProductDtoMapper implements Mapper<ImportProductDto, Product>{

   private final RatingReadMapper ratingReadMapper;

    @Override
    public Product map(ImportProductDto obj) {
        return Product.builder()
                .id(obj.getId())
                .title(obj.getTitle())
                .price(obj.getPrice())
                .description(obj.getDescription())
                .category(new Category(obj.getCategory()))
                .image(obj.getImage())
                .rating(ratingReadMapper.map(obj.getRating()))
                .build();
    }

    public ImportProductDto mapToDto(Product obj) {
        return ImportProductDto.builder()
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
