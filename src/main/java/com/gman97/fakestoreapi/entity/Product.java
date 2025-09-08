package com.gman97.fakestoreapi.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Builder
@NamedEntityGraph(name = "WithCategoryAndRating",
        attributeNodes = {
                @NamedAttributeNode("category"),
                @NamedAttributeNode("rating")
        })
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;

    private String description;

    private Double price;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    private String image;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "rate", referencedColumnName = "rate"),
            @JoinColumn(name = "count", referencedColumnName = "count")
    })
    private Rating rating;
}
