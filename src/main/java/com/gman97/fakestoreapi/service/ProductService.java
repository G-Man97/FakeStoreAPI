package com.gman97.fakestoreapi.service;

import com.gman97.fakestoreapi.dto.ProductDto;
import com.gman97.fakestoreapi.dto.ProductFilter;
import com.gman97.fakestoreapi.entity.Category;
import com.gman97.fakestoreapi.entity.Product;
import com.gman97.fakestoreapi.entity.Rating;
import com.gman97.fakestoreapi.entity.RatingId;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.*;

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
        var predicate = QPredicates.builder()
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

        /* Если в списке id продуктов, найденных в БД (existingProdIds)
         * есть id продуктов из списка импортированных продуктов (products),
         * то добавляем эти продукты в newProducts, чтобы сохранить их как новые товары,
         * но с оригинальными id.
         */

        List<Product> newProducts = new ArrayList<>(products.size());
        List<Product> oldProducts = new ArrayList<>(products.size());

        if (!existingProdIds.isEmpty()) {
            for (Product product : products) {
                if (!existingProdIds.contains(product.getId())) {
                    newProducts.add(product);
                } else {
                    oldProducts.add(product);
                }
            }
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

            // Составляем список полей сущности Product + поля RatingId (чтобы по ним тоже можно было сортировать)
            var productFields = new ArrayList<>(Arrays.stream(Product.class.getDeclaredFields())
                    .map(Field::getName)
                    .filter(e -> !"rating".equals(e))
                    .toList());
            productFields.addAll(Arrays.stream(RatingId.class.getDeclaredFields()).map(Field::getName).toList());

            // Если в параметре запроса пришло больше названий полей, чем есть у сущности,
            // то берем только первые N полей из списка, где N - кол-во полей у сущности
            orderBy = orderBy.size() > productFields.size() ? orderBy.subList(0, productFields.size() - 1) : orderBy;

            // Проверяем, есть ли переданые в запросе поля у сущности
            for (int i = 0; i < orderBy.size(); i++) {

                String element = orderBy.get(i);

                if (productFields.contains(element)) {
                    // Для сортировки правильно указываем поля rate count
                    if (element.equals("rate")) {
                        orderBy.set(i, "rating.rating.rate");
                    }
                    if (element.equals("count")) {
                        orderBy.set(i, "rating.rating.count");
                    }
                } else {
                    orderBy.remove(i); // Если такого поля нет, то удаляем его из списка
                }
            }

            if (!orderBy.isEmpty()) {

                direction = direction == null ? new ArrayList<>() : direction;

                // Размер списка direction не может быть меньше orderBy,
                // поэтому в таком случае дозаполняем его элементами "asc"
                if (orderBy.size() > direction.size()) {
                    for (int i = direction.size(); i < orderBy.size(); i++) {
                        direction.add("asc");
                    }
                }

                List<Sort.Order> orders = new ArrayList<>();

                for (int i = 0; i < orderBy.size(); i++) {
                    Sort.Direction dir = direction.get(i).equalsIgnoreCase("desc")
                            ? Sort.Direction.DESC
                            : Sort.Direction.ASC;
                    orders.add(new Sort.Order(dir, orderBy.get(i)));
                }

                return Optional.of(Sort.by(orders));
            }
        }
        return Optional.empty();
    }
}
