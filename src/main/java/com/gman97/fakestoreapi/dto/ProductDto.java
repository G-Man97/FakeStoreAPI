package com.gman97.fakestoreapi.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import static com.gman97.fakestoreapi.util.ValidationStringsUtil.NOT_BLANK;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class ProductDto {

    private Integer id;

    @NotBlank(message = "Поле title" + NOT_BLANK)
    @Size(min = 2, max = 128, message = "Поле title должно содержать от 2 до 128 символов")
    private String title;

    @NotBlank(message = "Поле description" + NOT_BLANK)
    private String description;

    @NotNull(message = "Поле price" + NOT_BLANK)
    @DecimalMin(value = "0.0", message = "Поле price должно содержать число от 0")
    @DecimalMax(value = "9999.99", message = "Поле price должно содержать число до 9999.99")
    private Double price;

    @NotBlank(message = "Поле category" + NOT_BLANK)
    @Size(min = 2, max = 64, message = "Поле category должно содержать от 2 до 64 символов")
    private String category;

    @NotBlank(message = "Поле image" + NOT_BLANK)
    private String image;

    @Valid
    private RatingReadDto rating;

}
