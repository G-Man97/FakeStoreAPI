package com.gman97.fakestoreapi.dto;

import com.gman97.fakestoreapi.validation.CheckOrderByAndDirection;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@CheckOrderByAndDirection
public class ProductFilter {

    @Parameter(description = "Показать товары более указанного ценника включительно")
    private Double minPrice;
    @Parameter(description = "Показать товары менее указанного ценника включительно")
    private Double maxPrice;
    @Parameter(description = "Показать товары только в указанной категории")
    private String categoryName;
    @Parameter(description = "Сортировка товаров по указанному полю")
    private List<String> orderBy;
    @Parameter(description = "Направление сортировки (asc либо desc)")
    private List<String> direction;
}
