package com.transfer.playlist.music.clients.spotify.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import com.transfer.playlist.music.clients.spotify.dto.auth.GetAccessTokenRequest;
import com.transfer.playlist.music.clients.spotify.dto.auth.GetAccessTokenResponse;
import com.transfer.playlist.music.clients.spotify.exception.SpotifyAuthException;

@Service
public class SpotifyAuthService {

    private final RestClient restClient;
    private final static String SPOTIFY_ACCOUNT_BASE_URL = "https://accounts.spotify.com/api";
    private final static String TOKEN_URI = "/token";
    
    private final String clientId;
    private final String clientSecret;

    public SpotifyAuthService(
        RestClient.Builder builder,
        @Value("${spotify.client-id}") String clientId,
        @Value("${spotify.client-secret}") String clientSecret
    ) {
        this.restClient = builder
            .baseUrl(SPOTIFY_ACCOUNT_BASE_URL)
            .build();
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public GetAccessTokenResponse getSpotifyAccessToken(GetAccessTokenRequest request) {
        String code = request.code();
        
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("redirect_uri", "https://google.com");
        formData.add("code", code);

        GetAccessTokenResponse response = restClient.post()
            .uri(TOKEN_URI)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .headers(headers -> headers.setBasicAuth(
                this.clientId,
                this.clientSecret
            ))
            .body(formData)
            .retrieve()
            .onStatus(status -> status.isError(), (req, res) -> {
                throw new SpotifyAuthException(res.getStatusText(), res.getStatusCode());
            })
            .body(GetAccessTokenResponse.class);

        return response;
    }
}
