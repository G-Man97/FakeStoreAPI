package com.gman97.fakestoreapi.http.rest;

import com.gman97.fakestoreapi.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/import")
@RequiredArgsConstructor
public class ImportController {

    private final ProductService productService;


    @Operation(summary = "Импортировать товары (только для пользователя с ролью администратора)",
            description = "В ответе возвращается пустое тело запроса")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Товары успешно импортированы")
    })
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public void importData() {
        productService.saveImportedProducts();
    }

}