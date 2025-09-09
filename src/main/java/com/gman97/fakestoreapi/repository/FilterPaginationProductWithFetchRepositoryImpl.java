package com.gman97.fakestoreapi.repository;

import com.gman97.fakestoreapi.entity.Product;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathBuilderFactory;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.hibernate.graph.GraphSemantic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.Querydsl;

import java.util.List;

import static com.gman97.fakestoreapi.entity.QProduct.product;

@RequiredArgsConstructor
public class FilterPaginationProductWithFetchRepositoryImpl implements FilterPaginationProductWithFetchRepository {

    private final EntityManager entityManager;

    @Override
    public Page<Product> findAll(Predicate predicate, Pageable pageable) {
        JPAQuery<Product> query = new JPAQuery<Product>(entityManager)
                .select(product)
                .from(product)
                .where(predicate);

        long total = query.fetchCount();

        Querydsl querydsl = new Querydsl(entityManager, (new PathBuilderFactory()).create(Product.class));

        querydsl.applySorting(pageable.getSort(), query);

        List<Product> products = query
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .setHint(GraphSemantic.LOAD.getJakartaHintName(), entityManager.getEntityGraph("WithCategoryAndRating"))
                .fetch();

        return new PageImpl<>(products, pageable, total);
    }
}
