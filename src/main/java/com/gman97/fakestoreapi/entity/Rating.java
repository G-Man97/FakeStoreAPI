package com.gman97.fakestoreapi.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "ratings")
@Setter
@Getter
@EqualsAndHashCode(of = {"rating"})
@NoArgsConstructor
@AllArgsConstructor
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private RateCount rating;
}
