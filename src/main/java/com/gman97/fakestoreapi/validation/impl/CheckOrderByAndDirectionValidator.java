package com.gman97.fakestoreapi.validation.impl;

import com.gman97.fakestoreapi.dto.ProductFilter;
import com.gman97.fakestoreapi.entity.Product;
import com.gman97.fakestoreapi.entity.RatingId;
import com.gman97.fakestoreapi.validation.CheckOrderByAndDirection;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class CheckOrderByAndDirectionValidator implements ConstraintValidator<CheckOrderByAndDirection, ProductFilter> {

    private static final List<String> PRODUCT_FIELDS;

    static {
        // Составляем список полей сущности Product + поля RatingId (чтобы по ним тоже можно было сортировать)
        PRODUCT_FIELDS = new ArrayList<>(Arrays.stream(Product.class.getDeclaredFields())
                .map(Field::getName)
                .filter(e -> !"rating".equals(e) && !"serialVersionUID".equals(e))
                .toList());
        PRODUCT_FIELDS.addAll(Arrays.stream(RatingId.class.getDeclaredFields()).map(Field::getName).toList());
    }

    @Override
    public boolean isValid(ProductFilter value, ConstraintValidatorContext context) {
        var orderBy = value.getOrderBy();
        var direction = value.getDirection();

        if (orderBy != null && !orderBy.isEmpty()) {

            // Если в параметре запроса пришло больше названий полей, чем есть у сущности,
            // то берем только первые N полей из списка, где N - кол-во полей у сущности
            orderBy = orderBy.size() > PRODUCT_FIELDS.size() ? orderBy.subList(0, PRODUCT_FIELDS.size() - 1) : orderBy;
            direction = direction == null ? new ArrayList<>() : direction;

            // Размер списка direction не может быть меньше orderBy,
            // поэтому в таком случае дозаполняем его элементами "asc"
            if (orderBy.size() > direction.size()) {
                for (int i = direction.size(); i < orderBy.size(); i++) {
                    direction.add("asc");
                }
            }

            // Проверяем, есть ли переданые в запросе поля у сущности
            for (int i = 0; i < orderBy.size(); i++) {

                String element = orderBy.get(i);

                if (PRODUCT_FIELDS.contains(element)) {
                    // Для сортировки правильно указываем поля rate и count
                    if (element.equals("rate")) {
                        orderBy.set(i, "rating.rating.rate");
                    }
                    if (element.equals("count")) {
                        orderBy.set(i, "rating.rating.count");
                    }
                } else {
                    orderBy.remove(i); // Если такого поля нет, то удаляем его из списка
                    direction.remove(i--); // Также удаляем направление сортировки для этого поля
                }
            }
        }
        return true;
    }
}
