package com.gman97.fakestoreapi.service;

import com.gman97.fakestoreapi.dto.ProductDto;
import com.gman97.fakestoreapi.dto.ProductFilter;
import com.gman97.fakestoreapi.entity.Category;
import com.gman97.fakestoreapi.entity.Product;
import com.gman97.fakestoreapi.entity.Rating;
import com.gman97.fakestoreapi.mapper.ProductCreateEditMapper;
import com.gman97.fakestoreapi.mapper.ProductReadMapper;
import com.gman97.fakestoreapi.mapper.RatingReadMapper;
import com.gman97.fakestoreapi.repository.CategoryRepository;
import com.gman97.fakestoreapi.repository.ProductRepository;
import com.gman97.fakestoreapi.repository.RatingRepository;
import com.gman97.fakestoreapi.util.QPredicatesUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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


    public Page<ProductDto> findAllByFilter(ProductFilter filter, Integer page, Integer size) {
        var predicate = QPredicatesUtil.builder()
                .add(filter.getMinPrice(), product.price::goe)
                .add(filter.getMaxPrice(), product.price::loe)
                .add(filter.getCategoryName(), product.category.name::equalsIgnoreCase)
                .build();

        Pageable pageable = prepareSort(filter.getOrderBy(), filter.getDirection())
                .map(sort -> PageRequest.of(page, size, sort))
                .orElse(PageRequest.of(page, size));

        return productRepository.findAll(predicate, pageable)
                .map(productReadMapper::map);
    }

    public Optional<ProductDto> findById(Integer id) {
        return productRepository.findById(id)
                .map(productReadMapper::map);
    }

    @Transactional
    public void saveImportedProducts(List<ProductDto> dtos) {

        List<Product> products = dtos.stream()
                .map(productCreateEditMapper::map)
                .toList();

        // Сначала сохраняем главные сущности над сущностью Product
        var existingCats = categoryRepository.findAllById(products.stream().map(p -> p.getCategory().getName()).toList());
        Set<Category> categories = products.stream()
                .map(Product::getCategory)
                .filter(e -> !existingCats.contains(e))
                .collect(toSet());
        if (!categories.isEmpty()) {
            categoryRepository.saveAllAndFlush(categories);
        }

        var existingRatings = ratingRepository.findAllById(products.stream().map(p -> p.getRating().getRating()).toList());
        Set<Rating> ratings = products.stream()
                .map(Product::getRating)
                .filter(e -> !existingRatings.contains(e))
                .collect(toSet());
        if (!ratings.isEmpty()) {
            ratingRepository.saveAllAndFlush(ratings);
        }

        /* Если в списке id продуктов, найденных в БД (existingProdIds)
         * есть id продуктов из списка импортированных продуктов (products),
         * то добавляем эти продукты в newProducts, чтобы сохранить их как новые товары,
         * но с оригинальными id.
         */

        var existingProdIds = productRepository.findAllById(products.stream().map(Product::getId).toList())
                .stream()
                .map(Product::getId)
                .toList();

        List<Product> newProducts = new ArrayList<>(products.size());
        List<Product> oldProducts = new ArrayList<>(products.size());

        if (!existingProdIds.isEmpty()) {
            products.forEach(e -> {
                if (existingProdIds.contains(e.getId())) {
                    oldProducts.add(e);
                } else {
                    newProducts.add(e);
                }
            });
        } else {
            /* Если в БД не нашли товары с id из списка импортированых товаров,
             * следовательно, это новые товары, поэтому добавляем их в newProducts
             */
            newProducts.addAll(products);
        }

        if (!newProducts.isEmpty()) {
            productRepository.saveImportedProducts(newProducts);
        }
        if (!oldProducts.isEmpty()) {
            productRepository.saveAll(oldProducts);
        }
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

    private Optional<Sort> prepareSort(List<String> orderBy, List<String> direction) {

        if (orderBy != null && !orderBy.isEmpty()) {

            List<Sort.Order> orders = new ArrayList<>(orderBy.size());

            for (int i = 0; i < orderBy.size(); i++) {
                Sort.Direction dir = direction.get(i).equalsIgnoreCase("desc")
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;
                orders.add(new Sort.Order(dir, orderBy.get(i)));
            }

            return Optional.of(Sort.by(orders));
        }
        return Optional.empty();
    }
}
