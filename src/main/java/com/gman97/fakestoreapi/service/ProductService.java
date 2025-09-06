package com.gman97.fakestoreapi.service;

import com.gman97.fakestoreapi.dto.ImportProductDto;
import com.gman97.fakestoreapi.entity.Category;
import com.gman97.fakestoreapi.entity.Product;
import com.gman97.fakestoreapi.entity.Rating;
import com.gman97.fakestoreapi.mapper.ImportProductDtoMapper;
import com.gman97.fakestoreapi.repository.CategoryRepository;
import com.gman97.fakestoreapi.repository.ProductRepository;
import com.gman97.fakestoreapi.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final RatingRepository ratingRepository;
    private final ImportProductDtoMapper importProductDtoMapper;

    @Transactional
    public List<ImportProductDto> saveImportedProducts(List<ImportProductDto> dtos) {

        List<Product> products = dtos.stream()
                .map(importProductDtoMapper::map)
                .sorted(Comparator.comparing(Product::getId))
                .toList();

        // Сначала сохраняем главные сущности над сущностью Product
        Set<Category> categories = products.stream()
                .map(Product::getCategory)
                .collect(toSet());
        categoryRepository.saveAllAndFlush(categories);

        Set<Rating> ratings = products.stream()
                .map(Product::getRating)
                .collect(toSet());
        ratingRepository.saveAllAndFlush(ratings);

        var productIds = products.stream()
                .map(Product::getId)
                .toList();
        var existingProdIds = productRepository.findAllByIds(productIds).stream()
                .map(Product::getId)
                .toList();

        /*
         * Если в списке id продуктов, найденных в БД (existingProdIds)
         * есть id продуктов из списка импортированных продуктов (products),
         * то зануляем id таких продуктов, чтобы сохранить их как новые товары.
         * Остальные продукты обновляются в БД
         */

        if (!existingProdIds.isEmpty()) {
            for (Product product : products) {
                if (!existingProdIds.contains(product.getId())) {
                    product.setId(null);
                }
            }
        } else {
            /*
             * Если в БД не нашли товары с id из списка импортированых товаров,
             * следовательно, это новые товары, поэтому зануляем им id
             */
            products = products.stream().peek(p -> p.setId(null)).toList();
        }

        products = productRepository.saveAll(products);

        return products.stream().map(importProductDtoMapper::mapToDto).toList();
    }

}
