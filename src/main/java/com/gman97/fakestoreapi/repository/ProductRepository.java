package com.gman97.fakestoreapi.repository;

import com.gman97.fakestoreapi.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer>, FilterPaginationProductWithFetchRepository {

    @Query("select p from Product p " +
           "join fetch p.category " +
           "join fetch p.rating " +
           "where p.id = :id")
    Optional<Product> findByIdWithFetch(Integer id);

    @Query("select p from Product p " +
           "where p.externalId in (:externalIds)")
    List<Product> findAllByExternalIds(List<Integer> externalIds);
}
