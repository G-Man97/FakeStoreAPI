package com.gman97.fakestoreapi.util;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindingResult;

import java.util.List;

public class ValidationStringsUtil {

    public static final String NOT_BLANK = " не должно быть пустым";

    public static List<String> getMessages(BindingResult bindingResult) {
        return bindingResult.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();
    }
}
