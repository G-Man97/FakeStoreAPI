package com.gman97.fakestoreapi.http.rest;

import com.gman97.fakestoreapi.dto.PageResponse;
import com.gman97.fakestoreapi.dto.ProductDto;
import com.gman97.fakestoreapi.dto.ProductFilter;
import com.gman97.fakestoreapi.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import static com.gman97.fakestoreapi.util.ValidationStringsUtil.*;


@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;


    @Tag(name = "GET-методы")
    @Operation(summary = "Просмотреть все товары",
            description = "В ответе возвращается список товаров. Параметры пагинации и фильтра " +
                          "можно задавать с помощью параметров ниже")
    @GetMapping
    public PageResponse<ProductDto> findAll(@ParameterObject @Valid ProductFilter filter,
            @Parameter(description = "Номер страницы") @RequestParam(required = false, defaultValue = "0") Integer page,
            @Parameter(description = "Размер страницы") @RequestParam(required = false, defaultValue = "5") Integer size) {

        Page<ProductDto> pageForResponse = productService.findAllByFilter(filter, page, size);
        return PageResponse.of(pageForResponse);
    }

    @Tag(name = "GET-методы")
    @Operation(summary = "Получить товар по его id",
            description = "В ответе возвращается товар с указанным id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Товар найден"),
            @ApiResponse(responseCode = "404", description = "Товар не найден")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Integer id) {
        return productService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Создать новый товар (только для пользователя с ролью администратора)",
            description = "В случае успеха в ответе возвращается созданный товар с присвоенным id")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Новый товар создан"),
            @ApiResponse(responseCode = "400", description = "Ошибки при заполнении полей")
    })
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> create(@RequestBody @Valid ProductDto productDto, BindingResult bindingResult) {
        return bindingResult.hasErrors()
                ? ResponseEntity.badRequest().body(getMessages(bindingResult))
                : ResponseEntity.status(HttpStatus.CREATED).body(productService.save(productDto));
    }

    @Operation(summary = "Обновить существующий товар (только для пользователя с ролью администратора)",
            description = "В случае успеха в ответе возвращается обновленный товар")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Товар успешно обновлен"),
            @ApiResponse(responseCode = "400", description = "Ошибки при заполнении полей"),
            @ApiResponse(responseCode = "404", description = "Товар с указанным id не найден")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> update(@PathVariable @Parameter(description = "Идентификатор товара") Integer id,
                                    @RequestBody @Valid ProductDto productDto,
                                    BindingResult bindingResult) {
        return bindingResult.hasErrors()
                ? ResponseEntity.badRequest().body(getMessages(bindingResult))
                : productService.update(id, productDto)
                                                    .map(ResponseEntity::ok)
                                                    .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Удалить существующий товар (только для пользователя с ролью администратора)",
            description = "В ответе возвращается пустое тело запроса")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Товар успешно удален"),
            @ApiResponse(responseCode = "404", description = "Товар с указанным id не найден")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable @Parameter(description = "Идентификатор товара") Integer id) {
        return productService.delete(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
