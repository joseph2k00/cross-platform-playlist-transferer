package com.transfer.playlist.music.clients.youtube.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.transfer.playlist.music.clients.youtube.dto.RedirectLinkResponse;
import com.transfer.playlist.music.clients.youtube.dto.auth.GetAccessTokenRequest;
import com.transfer.playlist.music.clients.youtube.dto.auth.GetAccessTokenResponse;

import jakarta.servlet.http.HttpSession;

@Service
public class AuthService {

    public final static String AUTH_BASE_URL = "https://accounts.google.com/o/oauth2/v2/auth";
    private final static String TOKEN_URL = "https://oauth2.googleapis.com/token";
    private final static String SCOPE = "https://www.googleapis.com/auth/youtube";

    public final String redirectUrl;
    public final String clientId;
    private final String clientSecret;
    private final RestClient restClient;

    public AuthService(
        RestClient.Builder builder,
        @Value("${youtube.redirect-urls}") String redirectUrl,
        @Value("${youtube.client-id}") String clientId,
        @Value("${youtube.client-secret}") String clientSecret
    ) {
        this.redirectUrl = redirectUrl;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.restClient = builder.build();
    }

    public RedirectLinkResponse generateLink() {
        String link = UriComponentsBuilder.fromUriString(AUTH_BASE_URL)
            .queryParam("client_id", clientId)
            .queryParam("redirect_uri", redirectUrl)
            .queryParam("response_type", "code")
            .queryParam("scope", SCOPE)
            .queryParam("access_type", "online")
            .build()
            .toUriString();

        return new RedirectLinkResponse(link);
    }

    public void getYoutubeAccessToken(GetAccessTokenRequest request, HttpSession session) {
        String code = request.code();

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("redirect_uri", redirectUrl);
        formData.add("code", code);

        GetAccessTokenResponse response = restClient.post()
            .uri(TOKEN_URL)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(formData)
            .retrieve()
            .body(GetAccessTokenResponse.class);

        session.setAttribute(
            YoutubeApiService.YOUTUBE_ACCESS_TOKEN_SESSION_KEY,
            response.accessToken()
        );
    }
}
