package com.gman97.fakestoreapi.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class ProductDto {

    private Integer id;

    private String title;

    private String description;

    private Double price;

    private String category;

    private String image;

    private RatingReadDto rating;

}
