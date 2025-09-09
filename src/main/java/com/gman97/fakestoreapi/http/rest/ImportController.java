package com.gman97.fakestoreapi.http.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gman97.fakestoreapi.dto.ProductDto;
import com.gman97.fakestoreapi.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

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
    @Scheduled(fixedRateString = "PT30M", initialDelayString = "PT30M")
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
