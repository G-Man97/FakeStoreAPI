package com.gman97.fakestoreapi.repository;

import com.gman97.fakestoreapi.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, String> {

    @Query("select c from Category c " +
           "where c.name in (:names)")
    List<Category> findAllByNames(List<String> names);

    Optional<Category> findByName(String name);
}
