package com.gman97.fakestoreapi.repository;

import com.gman97.fakestoreapi.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer>, ProductImportRepository,
                                                                            FilterPaginationProductWithFetchRepository {
}
