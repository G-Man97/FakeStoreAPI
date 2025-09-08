package com.gman97.fakestoreapi.service;

import com.gman97.fakestoreapi.dto.ProductDto;
import com.gman97.fakestoreapi.entity.Category;
import com.gman97.fakestoreapi.entity.Product;
import com.gman97.fakestoreapi.entity.Rating;
import com.gman97.fakestoreapi.mapper.ProductCreateEditMapper;
import com.gman97.fakestoreapi.mapper.ProductReadMapper;
import com.gman97.fakestoreapi.mapper.RatingReadMapper;
import com.gman97.fakestoreapi.repository.CategoryRepository;
import com.gman97.fakestoreapi.repository.ProductRepository;
import com.gman97.fakestoreapi.repository.RatingRepository;
import com.gman97.fakestoreapi.util.QPredicates;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.gman97.fakestoreapi.entity.QProduct.product;
import static java.util.stream.Collectors.toSet;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final RatingRepository ratingRepository;
    private final ProductReadMapper productReadMapper;
    private final RatingReadMapper ratingReadMapper;
    private final ProductCreateEditMapper productCreateEditMapper;


    public Page<ProductDto> findAll(Double minPrice, Double maxPrice, Integer page, Integer size) {
        var predicate = QPredicates.builder()
                .add(minPrice, product.price::goe)
                .add(maxPrice, product.price::loe)
                .build();

        return productRepository.findAll(predicate, PageRequest.of(page, size))
                .map(productReadMapper::map);
    }

    public Optional<ProductDto> findById(Integer id) {
        return productRepository.findById(id)
                .map(productReadMapper::map);
    }

    public Page<ProductDto> findAllByCategoryName(String categoryName, Integer page, Integer size) {
        var predicate = QPredicates.builder()
                .add(categoryName.trim(), product.category.name::equalsIgnoreCase)
                .build();

        return productRepository.findAll(predicate, PageRequest.of(page, size))
                .map(productReadMapper::map);
    }

    @Transactional
    public void saveImportedProducts(List<ProductDto> dtos) {

        List<Product> products = dtos.stream()
                .map(productCreateEditMapper::map)
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

        productRepository.saveAll(products);
    }

    @Transactional
    public ProductDto save(ProductDto dto) {
        return Optional.of(dto)
                .map(product -> {
                    categoryRepository.save(new Category(dto.getCategory()));
                    ratingRepository.saveAndFlush(ratingReadMapper.map(dto.getRating()));
                    return productCreateEditMapper.map(dto);
                })
                .map(productRepository::saveAndFlush)
                .map(productReadMapper::map)
                .orElseThrow(RuntimeException::new);
    }

    @Transactional
    public Optional<ProductDto> update(Integer id, ProductDto dto) {
        return productRepository.findById(id)
                .map(entity -> {
                    categoryRepository.save(new Category(dto.getCategory()));
                    ratingRepository.saveAndFlush(ratingReadMapper.map(dto.getRating()));
                    return productCreateEditMapper.map(dto, entity);
                })
                .map(productRepository::saveAndFlush)
                .map(productReadMapper::map);
    }

    @Transactional
    public boolean delete(Integer id) {
        return productRepository.findById(id)
                .map(product -> {
                    productRepository.delete(product);
                    productRepository.flush();
                    return true;
                })
                .orElse(false);
    }
}
