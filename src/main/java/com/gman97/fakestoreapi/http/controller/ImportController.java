package com.gman97.fakestoreapi.http.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gman97.fakestoreapi.dto.ImportProductDto;
import com.gman97.fakestoreapi.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

@Controller
@RequestMapping("/import")
@RequiredArgsConstructor
public class ImportController {

    private final ProductService productService;

    @GetMapping
    public String importData() {

        var productDtos = getImportData();
        productDtos = productService.saveImportedProducts(productDtos);

        System.out.println(productDtos);


        return "products";
    }

    private List<ImportProductDto> getImportData() {
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
