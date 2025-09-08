package com.gman97.fakestoreapi.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "ratings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Rating {

    @EmbeddedId
    private RatingId rating;

}
