package com.gman97.fakestoreapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record UserCreateDto(
        @NotBlank(message = "Вы пропустили поле Email!")
        @Email(message = "Введён некорректный email!")
        String username,

        @Size(min = 8, max = 16, message = "Длина пароля должна быть от 8 до 16 символов!")
        String rowPassword) {

}