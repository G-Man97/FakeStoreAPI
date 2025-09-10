package com.gman97.fakestoreapi.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.gman97.fakestoreapi.util.ValidationStringsUtil.NOT_BLANK;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class RatingReadDto {

    @NotNull(message = "Поле rate" + NOT_BLANK)
    @DecimalMin(value = "0.0", message = "Поле rate должно содержать число от 0")
    @DecimalMax(value = "5.0", message = "Поле rate должно содержать число до 5.0")
    private Double rate;

    @NotNull(message = "Поле count" + NOT_BLANK)
    @DecimalMin(value = "0", message = "Поле count должно содержать число от 0")
    @DecimalMax(value = "10000", message = "Поле count должно содержать число до 10000")
    private Integer count;
}
