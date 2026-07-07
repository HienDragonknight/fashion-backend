package com.fashion.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fashion.config.OAuthProperties;
import com.fashion.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

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

    public OAuthProfile verifyFacebookToken(String accessToken) {
        String appId = oauthProperties.getFacebook().getAppId();
        String appSecret = oauthProperties.getFacebook().getAppSecret();
        if (appId == null || appId.isBlank() || appSecret == null || appSecret.isBlank()) {
            throw new BusinessException("Facebook OAuth chưa được cấu hình trên server");
        }

        String appAccessToken = appId + "|" + appSecret;
        JsonNode debug = restClient.get()
                .uri(UriComponentsBuilder
                        .fromUriString("https://graph.facebook.com/debug_token")
                        .queryParam("input_token", accessToken)
                        .queryParam("access_token", appAccessToken)
                        .build()
                        .toUri())
                .retrieve()
                .body(JsonNode.class);

        if (debug == null || !debug.path("data").path("is_valid").asBoolean(false)) {
            throw new BusinessException("Facebook token không hợp lệ");
        }

        String tokenAppId = debug.path("data").path("app_id").asText("");
        if (!appId.equals(tokenAppId)) {
            throw new BusinessException("Facebook token không khớp ứng dụng");
        }

        JsonNode profile = restClient.get()
                .uri(UriComponentsBuilder
                        .fromUriString("https://graph.facebook.com/me")
                        .queryParam("fields", "id,name,email,picture.type(large)")
                        .queryParam("access_token", accessToken)
                        .build()
                        .toUri())
                .retrieve()
                .body(JsonNode.class);

        if (profile == null || profile.has("error")) {
            throw new BusinessException("Không thể lấy thông tin Facebook");
        }

        String providerId = profile.path("id").asText("");
        String email = profile.path("email").asText(null);
        String name = profile.path("name").asText("Người dùng Facebook");
        String picture = profile.path("picture").path("data").path("url").asText(null);

        return new OAuthProfile("FACEBOOK", providerId, email, name, picture);
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
