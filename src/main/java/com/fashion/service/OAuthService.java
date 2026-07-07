package com.fashion.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fashion.config.OAuthProperties;
import com.fashion.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class OAuthService {

    private final OAuthProperties oauthProperties;
    private final RestClient restClient = RestClient.create();

    public OAuthProfile verifyGoogleToken(String idToken) {
        String clientId = oauthProperties.getGoogle().getClientId();
        if (clientId == null || clientId.isBlank()) {
            throw new BusinessException("Google OAuth chưa được cấu hình trên server");
        }

        JsonNode payload = restClient.get()
                .uri("https://oauth2.googleapis.com/tokeninfo?id_token={token}", idToken)
                .retrieve()
                .body(JsonNode.class);

        if (payload == null || payload.has("error")) {
            throw new BusinessException("Google token không hợp lệ");
        }

        String audience = payload.path("aud").asText("");
        if (!clientId.equals(audience)) {
            throw new BusinessException("Google token không khớp ứng dụng");
        }

        if (!"true".equalsIgnoreCase(payload.path("email_verified").asText())) {
            throw new BusinessException("Email Google chưa được xác minh");
        }

        return new OAuthProfile(
                "GOOGLE",
                payload.path("sub").asText(""),
                payload.path("email").asText(""),
                payload.path("name").asText("Người dùng Google"),
                payload.path("picture").asText(null)
        );
    }

    public record OAuthProfile(
            String provider,
            String providerId,
            String email,
            String fullName,
            String avatarUrl
    ) {
        public String resolveEmail() {
            if (email != null && !email.isBlank()) {
                return email.toLowerCase();
            }
            return provider.toLowerCase() + "_" + providerId + "@oauth.local";
        }
    }
}
