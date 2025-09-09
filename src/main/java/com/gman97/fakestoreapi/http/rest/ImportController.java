package com.gman97.fakestoreapi.http.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gman97.fakestoreapi.dto.ProductDto;
import com.gman97.fakestoreapi.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

@RestController
@RequestMapping("/api/v1/import")
@RequiredArgsConstructor
public class ImportController {

    private final ProductService productService;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public void importData() {
        productService.saveImportedProducts(getImportData());
    }

    private List<ProductDto> getImportData() {
        try {
            StringBuilder json = new StringBuilder();
            URL url = new URL("https://fakestoreapi.com/products");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.connect();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                json.append(line);
            }
            in.close();

            String jsonStr = new String(json);
            ObjectMapper objectMapper = new ObjectMapper();

            return objectMapper.readValue(jsonStr, new TypeReference<>() {});

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
