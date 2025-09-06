package com.gman97.fakestoreapi.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class RatingId implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("rate")
    private Double rate;

    @JsonProperty("count")
    private Integer count;
}
