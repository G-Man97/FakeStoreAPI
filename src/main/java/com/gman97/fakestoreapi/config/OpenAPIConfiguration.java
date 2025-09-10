package com.gman97.fakestoreapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfiguration {

    @Bean
    public OpenAPI defineOpenAPI () {

        Contact myContact = new Contact();
        myContact.setName("Малыхин Георгий");
        myContact.setEmail("geramal1221@gmail.com");

        Info info = new Info()
                .title("Products Store API")
                .version("1.0")
                .description("Это API предоставляет эндпоинты для импорта сведений о товарах из внешнего сервиса " +
                             "“Fake Store API” (https://fakestoreapi.com), сохранения их в базу данных, " +
                             "и других операций с этой информацией.")
                .contact(myContact);
        return new OpenAPI().info(info);
    }
}
