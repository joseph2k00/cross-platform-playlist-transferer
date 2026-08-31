package com.transfer.playlist.music.clients.spotify.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.transfer.playlist.music.clients.spotify.dto.RedirectLinkResponse;
import com.transfer.playlist.music.clients.spotify.dto.auth.GetAccessTokenRequest;
import com.transfer.playlist.music.clients.spotify.dto.auth.GetAccessTokenResponse;
import com.transfer.playlist.music.clients.spotify.exception.SpotifyAuthException;

@Service
public class SpotifyAuthService {

    private final RestClient restClient;
    private final static String SPOTIFY_ACCOUNT_BASE_URL = "https://accounts.spotify.com/api";
    private final static String SPOTIFY_AUTHORIZE_BASE_URL = "https://accounts.spotify.com/authorize";
    private final static String SCOPE = "playlist-read-private playlist-read-collaborative playlist-modify-private playlist-modify-public";
    private final static String TOKEN_URI = "/token";
    
    private final String clientId;
    private final String clientSecret;
    private final String redirectUri;

    public SpotifyAuthService(
        RestClient.Builder builder,
        @Value("${spotify.client-id}") String clientId,
        @Value("${spotify.client-secret}") String clientSecret,
        @Value("${spotify.redirect-uri}") String redirectUri
    ) {
        this.restClient = builder
            .baseUrl(SPOTIFY_ACCOUNT_BASE_URL)
            .build();
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
    }

    public RedirectLinkResponse generateLink() {
        String link = UriComponentsBuilder.fromUriString(SPOTIFY_AUTHORIZE_BASE_URL)
            .queryParam("client_id", clientId)
            .queryParam("response_type", "code")
            .queryParam("redirect_uri", redirectUri)
            .queryParam("scope", SCOPE)
            .build()
            .toUriString();

        return new RedirectLinkResponse(link);
    }

    public GetAccessTokenResponse getSpotifyAccessToken(GetAccessTokenRequest request) {
        String code = request.code();
        
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("redirect_uri", redirectUri);
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
