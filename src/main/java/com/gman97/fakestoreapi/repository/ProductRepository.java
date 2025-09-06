package com.gman97.fakestoreapi.repository;

import com.gman97.fakestoreapi.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    @Query("select p from Product p " +
            "where p.id in (:ids)")
    List<Product> findAllByIds(List<Integer> ids);
}
