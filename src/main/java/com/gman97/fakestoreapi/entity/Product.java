package com.gman97.fakestoreapi.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
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
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;

    private String description;

    private Double price;

    @ManyToOne(/*cascade = CascadeType.ALL*/)
    @JoinColumn(name = "category_id")
//    @JsonProperty("category")
    private Category category;

    private String image;

    @ManyToOne(/*cascade = CascadeType.ALL*/)
    @JoinColumns({
            @JoinColumn(name = "rate", referencedColumnName = "rate"),
            @JoinColumn(name = "count", referencedColumnName = "count")
    })
    private Rating rating;

//    @JsonSetter("category")
//    public void setCategoryEntity(@JsonProperty("category") String categoryName) {
//        this.category = new Category(categoryName);
//    }
}
