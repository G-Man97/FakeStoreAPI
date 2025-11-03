package com.gman97.fakestoreapi.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class RateCount {

    private Double rate;

    private Integer count;
}
