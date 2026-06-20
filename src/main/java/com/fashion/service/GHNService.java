package com.fashion.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class GHNService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${ghn.api-url}")
    private String apiUrl;

    @Value("${ghn.token}")
    private String token;

    @Value("${ghn.shop-id}")
    private String shopId;

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Token", token);
        headers.set("ShopId", shopId);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    public JsonNode getProvinces() {
        ResponseEntity<JsonNode> response = restTemplate.exchange(
                apiUrl + "/master-data/province",
                HttpMethod.GET,
                new HttpEntity<>(getHeaders()),
                JsonNode.class
        );
        return response.getBody();
    }

    public JsonNode getDistricts(Integer provinceId) {
        Map<String, Object> body = Map.of("province_id", provinceId);
        ResponseEntity<JsonNode> response = restTemplate.exchange(
                apiUrl + "/master-data/district",
                HttpMethod.POST,
                new HttpEntity<>(body, getHeaders()),
                JsonNode.class
        );
        return response.getBody();
    }

    public JsonNode getWards(Integer districtId) {
        Map<String, Object> body = Map.of("district_id", districtId);
        ResponseEntity<JsonNode> response = restTemplate.exchange(
                apiUrl + "/master-data/ward",
                HttpMethod.POST,
                new HttpEntity<>(body, getHeaders()),
                JsonNode.class
        );
        return response.getBody();
    }

    public JsonNode calculateShippingFee(
            Integer toDistrictId, String toWardCode, Integer weight, Integer value) {
        Map<String, Object> body = Map.of(
                "to_district_id", toDistrictId,
                "to_ward_code", toWardCode,
                "weight", weight,
                "insurance_value", value,
                "service_type_id", 2
        );
        ResponseEntity<JsonNode> response = restTemplate.exchange(
                apiUrl + "/v2/shipping-order/fee",
                HttpMethod.POST,
                new HttpEntity<>(body, getHeaders()),
                JsonNode.class
        );
        return response.getBody();
    }
}
