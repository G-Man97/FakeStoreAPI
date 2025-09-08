package com.gman97.fakestoreapi.mapper;

import com.gman97.fakestoreapi.dto.ProductDto;
import com.gman97.fakestoreapi.entity.Category;
import com.gman97.fakestoreapi.entity.Product;
import com.gman97.fakestoreapi.entity.Rating;
import com.gman97.fakestoreapi.entity.RatingId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductCreateEditMapper implements Mapper<ProductDto, Product> {

    @Override
    public Product map(ProductDto fromObject, Product toObject) {
        copy(fromObject, toObject);
        return toObject;
    }

    @Override
    public Product map(ProductDto obj) {
        Product product = new Product();
        copy(obj, product);
        product.setId(obj.getId());
        return product;

    }

    private void copy(ProductDto obj, Product product) {
        product.setTitle(obj.getTitle());
        product.setPrice(obj.getPrice());
        product.setDescription(obj.getDescription());
        product.setCategory(new Category(obj.getCategory().trim()));
        product.setImage(obj.getImage());
        product.setRating(new Rating(
                new RatingId(
                        obj.getRating().getRate(),
                        obj.getRating().getCount()
                ))
        );
    }
}
