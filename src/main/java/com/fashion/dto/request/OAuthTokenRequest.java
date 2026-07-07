package com.fashion.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OAuthTokenRequest {

    @NotBlank(message = "Token không được để trống")
    private String token;
}
