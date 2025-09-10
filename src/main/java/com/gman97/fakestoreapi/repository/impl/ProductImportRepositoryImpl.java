package com.gman97.fakestoreapi.repository.impl;

import com.gman97.fakestoreapi.entity.Product;
import com.gman97.fakestoreapi.repository.ProductImportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class ProductImportRepositoryImpl implements ProductImportRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final String SETVAL_SQL = "SELECT SETVAL('products_id_seq', %s);";
    private static final String SETVAL_MAX_SQL = "SELECT SETVAL('products_id_seq', (SELECT MAX(id) FROM products));";

    private static final String SAVE_PRODUCT_SQL = """
            INSERT INTO products (id, title, price, description, category_id, image, rate, count)
            VALUES ((SELECT nextval('products_id_seq')), :title, :price, :description, :category_id, :image, :rate, :count)
            """;

    @Override
    public void saveImportedProducts(List<Product> products) {
        try {
            Map<String, Object> params = new HashMap<>();

            for (Product product : products) {

                namedParameterJdbcTemplate.getJdbcTemplate().execute(SETVAL_SQL.formatted(product.getId() - 1));

                params.put("title", product.getTitle());
                params.put("price", product.getPrice());
                params.put("description", product.getDescription());
                params.put("category_id", product.getCategory().getName());
                params.put("image", product.getImage());
                params.put("rate", product.getRating().getRating().getRate());
                params.put("count", product.getRating().getRating().getCount());

                namedParameterJdbcTemplate.update(SAVE_PRODUCT_SQL, params);
            }
        } finally {
            namedParameterJdbcTemplate.getJdbcTemplate().execute(SETVAL_MAX_SQL);
        }
    }
}
