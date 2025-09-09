package com.gman97.fakestoreapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductFilter {

    private Double minPrice;
    private Double maxPrice;
    private String categoryName;
    private List<String> orderBy;
    private List<String> direction;
}
