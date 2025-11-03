package com.gman97.fakestoreapi.mapper;

import com.gman97.fakestoreapi.dto.RatingReadDto;
import com.gman97.fakestoreapi.entity.RateCount;
import com.gman97.fakestoreapi.entity.Rating;
import org.springframework.stereotype.Component;

@Component
public class RatingReadMapper implements Mapper<RatingReadDto, Rating> {

    @Override
    public Rating map(RatingReadDto obj) {
        return new Rating(null,
                new RateCount(
                        obj.getRate(),
                        obj.getCount()
                ));
    }

    public RatingReadDto mapToDto(Rating obj) {
        return new RatingReadDto(
                obj.getRating().getRate(),
                obj.getRating().getCount()
        );
    }
}
