package com.gman97.fakestoreapi.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "ratings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rating {

    @EmbeddedId
    private RatingId rating;

}
