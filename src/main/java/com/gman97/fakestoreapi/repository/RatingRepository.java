package com.gman97.fakestoreapi.repository;

import com.gman97.fakestoreapi.entity.RateCount;
import com.gman97.fakestoreapi.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long>{

    @Query("select r from Rating r " +
           "where r.rating in (:rateCounts)")
    List<Rating> findAllByRateAndCount(List<RateCount> rateCounts);

    @Query("select r from Rating r " +
           "where r.rating = :rateCount")
    Optional<Rating> findByRateAndCount(RateCount rateCount);
}
