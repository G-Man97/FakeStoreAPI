package com.gman97.fakestoreapi.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@NamedEntityGraph(name = "WithCategoryAndRating",
        attributeNodes = {
                @NamedAttributeNode("category"),
                @NamedAttributeNode("rating")
        })
public class Product {

    @Id
    @GeneratedValue(generator = "product_id_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "product_id_seq", sequenceName = "products_id_seq", initialValue = 0, allocationSize = 1)
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
