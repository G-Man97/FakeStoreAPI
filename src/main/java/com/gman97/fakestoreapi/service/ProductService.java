package com.gman97.fakestoreapi.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gman97.fakestoreapi.dto.ProductDto;
import com.gman97.fakestoreapi.dto.ProductFilter;
import com.gman97.fakestoreapi.entity.Category;
import com.gman97.fakestoreapi.entity.Product;
import com.gman97.fakestoreapi.entity.Rating;
import com.gman97.fakestoreapi.mapper.ImportProductMapper;
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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.gman97.fakestoreapi.entity.QProduct.product;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductService {

    private final ObjectMapper objectMapper;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final RatingRepository ratingRepository;
    private final ProductReadMapper productReadMapper;
    private final RatingReadMapper ratingReadMapper;
    private final ImportProductMapper importProductMapper;
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
        return productRepository.findByIdWithFetch(id)
                .map(productReadMapper::map);
    }

    @Transactional
    @Scheduled(fixedRateString = "PT30M", initialDelayString = "PT30M")
    public void saveImportedProducts() {

        List<Product> products = getImportData().stream()
                .map(importProductMapper::map)
                .toList();

        // Сначала сохраняем главные сущности над сущностью Product
        var existingCats = categoryRepository.findAllByNames(products.stream()
                .map(p -> p.getCategory().getName())
                .distinct()
                .toList());
        var allCats = new ArrayList<>(products.stream()
                .map(Product::getCategory)
                .distinct()
                .filter(e -> !existingCats.contains(e))
                .map(categoryRepository::save)
                .toList());
        categoryRepository.flush();
        allCats.addAll(existingCats);
        var allCatNames = allCats.stream().map(Category::getName).toList();

        var existingRatings = ratingRepository.findAllByRateAndCount(products.stream()
                .map(p -> p.getRating().getRating())
                .distinct()
                .toList());
        var allRatings = new ArrayList<>(products.stream()
                .map(Product::getRating)
                .distinct()
                .filter(e -> !existingRatings.contains(e))
                .map(ratingRepository::save)
                .toList());
        ratingRepository.flush();
        allRatings.addAll(existingRatings);
        var allRateCounts = allRatings.stream().map(Rating::getRating).toList();

        /* Если в списке externalId товаров, найденных в БД (existingProdIds)
         * есть externalId товаров из списка импортированных товаров (products),
         * то устанавливем id импортированным товарам (products) из списка твоаров
         * найденных в БД (existingProdWithExtIds). По схожему принципу утсанавливаем поля category и rating
         */
        var existingProdWithExtIds = productRepository.findAllByExternalIds(products.stream()
                .map(Product::getExternalId)
                .toList());
        var existingProdIds = existingProdWithExtIds.stream()
                .map(Product::getExternalId)
                .toList();

        int index;

        for (Product product : products) {
            if ((index = existingProdIds.indexOf(product.getExternalId())) != -1) {
                product.setId(existingProdWithExtIds.get(index).getId());
            }
            index = allCatNames.indexOf(product.getCategory().getName());
            product.setCategory(allCats.get(index));

            index = allRateCounts.indexOf(product.getRating().getRating());
            product.setRating(allRatings.get(index));
        }

        productRepository.saveAll(products);
    }

    @Transactional
    public ProductDto save(ProductDto dto) {
        return Optional.of(dto)
                .map(it -> {
                    var category = categoryRepository.findByName(it.getCategory())
                            .orElseGet(() -> categoryRepository.saveAndFlush(new Category(null, it.getCategory())));
                    var rating = ratingRepository.findByRateAndCount(ratingReadMapper.map(it.getRating()).getRating())
                            .orElseGet(() -> ratingRepository.saveAndFlush(ratingReadMapper.map(it.getRating())));

                    var product = productCreateEditMapper.map(it);
                    product.setCategory(category);
                    product.setRating(rating);

                    return product;
                })
                .map(productRepository::saveAndFlush)
                .map(productReadMapper::map)
                .orElseThrow(RuntimeException::new);
    }

    @Transactional
    public Optional<ProductDto> update(Integer id, ProductDto dto) {
        return productRepository.findById(id)
                .map(entity -> {
                    var category = categoryRepository.findByName(dto.getCategory())
                            .orElseGet(() -> categoryRepository.saveAndFlush(new Category(null, dto.getCategory())));
                    var rating = ratingRepository.findByRateAndCount(ratingReadMapper.map(dto.getRating()).getRating())
                            .orElseGet(() -> ratingRepository.saveAndFlush(ratingReadMapper.map(dto.getRating())));

                    var product = productCreateEditMapper.map(dto, entity);
                    product.setCategory(category);
                    product.setRating(rating);

                    return product;
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

    public List<ProductDto> getImportData() {
        try {
            StringBuilder json = new StringBuilder();
            URL url = new URI("https://fakestoreapi.com/products").toURL();

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.connect();

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line;

            while ((line = in.readLine()) != null) {
                json.append(line);
            }
            in.close();

            return objectMapper.readValue(new String(json), new TypeReference<>() {});

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
