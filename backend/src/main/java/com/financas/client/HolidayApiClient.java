package com.financas.client;

import com.financas.dto.HolidayDTO;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

@Component
public class HolidayApiClient {

    private final RestTemplate restTemplate;
    private static final String BRASIL_API_URL = "https://brasilapi.com.br/api/feriados/v1/";

    public HolidayApiClient(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(5))
                .build();
    }

    public List<HolidayDTO> getHolidaysByYear(int year) {
        try {
            ResponseEntity<List<HolidayDTO>> response = restTemplate.exchange(
                    BRASIL_API_URL + year,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<HolidayDTO>>() {}
            );
            return response.getBody() != null ? response.getBody() : Collections.emptyList();
        } catch (RestClientException e) {
            // Caso a API esteja fora do ar, retornamos uma lista vazia
            System.err.println("Erro ao buscar feriados para o ano " + year + ": " + e.getMessage());
            return Collections.emptyList();
        }
    }
}
