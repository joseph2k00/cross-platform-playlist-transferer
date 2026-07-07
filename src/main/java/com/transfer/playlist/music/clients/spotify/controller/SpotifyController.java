package com.transfer.playlist.music.clients.spotify.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.transfer.playlist.music.clients.spotify.dto.GetAccessTokenRequest;
import com.transfer.playlist.music.clients.spotify.dto.GetAccessTokenResponse;
import com.transfer.playlist.music.clients.spotify.service.SpotifyAuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/spotify")
public class SpotifyController {

    private final SpotifyAuthService authService;

    public SpotifyController(
        SpotifyAuthService authService
    ) {
        this.authService = authService;
    }

    @PostMapping("/get-access-token")
    public GetAccessTokenResponse getAccessToken(
        @Valid @RequestBody GetAccessTokenRequest request
    ) {
        return authService.getSpotifyAccessToken(request);
    }
}
