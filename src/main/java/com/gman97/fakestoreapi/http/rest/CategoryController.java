package com.gman97.fakestoreapi.http.rest;

import com.gman97.fakestoreapi.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @Tag(name = "GET-методы")
    @Operation(summary = "Показать все уникальные категории",
            description = "В случае успеха возвращается список уникальных категорий")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Категории найдены")
    })
    @GetMapping
    public List<String> findAll() {
        return categoryService.findAll();
    }

}
