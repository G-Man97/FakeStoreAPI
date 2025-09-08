package com.gman97.fakestoreapi.service;

import com.gman97.fakestoreapi.entity.Category;
import com.gman97.fakestoreapi.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<String> findAll() {
        return categoryRepository.findAll().stream()
                .map(Category::getName)
                .toList();
    }

}
