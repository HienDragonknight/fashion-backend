package com.fashion.controller;

import com.fashion.dto.response.ApiResponse;
import com.fashion.service.GHNService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/ghn")
@RequiredArgsConstructor
public class GHNController {

    private final GHNService ghnService;

    @GetMapping("/provinces")
    public ResponseEntity<ApiResponse<JsonNode>> getProvinces() {
        return ResponseEntity.ok(ApiResponse.success(ghnService.getProvinces()));
    }

    @PostMapping("/districts")
    public ResponseEntity<ApiResponse<JsonNode>> getDistricts(@RequestBody Map<String, Integer> body) {
        return ResponseEntity.ok(ApiResponse.success(ghnService.getDistricts(body.get("provinceId"))));
    }

    @PostMapping("/wards")
    public ResponseEntity<ApiResponse<JsonNode>> getWards(@RequestBody Map<String, Integer> body) {
        return ResponseEntity.ok(ApiResponse.success(ghnService.getWards(body.get("districtId"))));
    }

    @PostMapping("/shipping-fee")
    public ResponseEntity<ApiResponse<JsonNode>> getShippingFee(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(ApiResponse.success(
                ghnService.calculateShippingFee(
                        (Integer) body.get("districtId"),
                        (String) body.get("wardCode"),
                        (Integer) body.get("weight"),
                        (Integer) body.get("value"))));
    }
}
