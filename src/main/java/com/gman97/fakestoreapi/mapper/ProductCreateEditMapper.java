package com.gman97.fakestoreapi.mapper;

import com.gman97.fakestoreapi.dto.ProductDto;
import com.gman97.fakestoreapi.entity.Product;
import org.springframework.stereotype.Component;

@Component
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
        return product;

    }

    private void copy(ProductDto obj, Product product) {
        product.setTitle(obj.getTitle());
        product.setPrice(obj.getPrice());
        product.setDescription(obj.getDescription());
        product.setImage(obj.getImage());
    }
}
