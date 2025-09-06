package com.gman97.fakestoreapi.repository;

import com.gman97.fakestoreapi.entity.Rating;
import com.gman97.fakestoreapi.entity.RatingId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingRepository extends JpaRepository<Rating, RatingId> {
}
