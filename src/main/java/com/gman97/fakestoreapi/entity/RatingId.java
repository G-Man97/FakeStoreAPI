package com.gman97.fakestoreapi.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingId implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Double rate;

    private Integer count;
}
