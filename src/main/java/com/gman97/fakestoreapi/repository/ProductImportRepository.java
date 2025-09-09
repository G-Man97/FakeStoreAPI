package com.gman97.fakestoreapi.repository;

import com.gman97.fakestoreapi.entity.Product;

import java.util.List;

public interface ProductImportRepository {

    void saveImportedProducts(List<Product> products);
}
