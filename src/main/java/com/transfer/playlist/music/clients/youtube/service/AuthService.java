package com.transfer.playlist.music.clients.youtube.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import com.transfer.playlist.music.clients.youtube.dto.RedirectLinkResponse;

@Service
public class AuthService {

    public final static String AUTH_BASE_URL = "https://accounts.google.com/o/oauth2/v2/auth";
    private final static String SCOPE = "https://www.googleapis.com/auth/youtube";

    public final String redirectUrl;
    public final String clientId;

    public AuthService(
        @Value("${youtube.redirect-urls}") String redirectUrl,
        @Value("${youtube.client-id}") String clientId
    ) {
        this.redirectUrl = redirectUrl;
        this.clientId = clientId;
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
}
