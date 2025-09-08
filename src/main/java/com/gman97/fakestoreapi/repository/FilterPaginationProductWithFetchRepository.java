package com.gman97.fakestoreapi.repository;

import com.gman97.fakestoreapi.entity.Product;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FilterPaginationProductWithFetchRepository {

    Page<Product> findAll(Predicate predicate, Pageable pageable);
}
