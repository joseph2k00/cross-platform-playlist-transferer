package com.transfer.playlist.music.clients.spotify.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.transfer.playlist.music.clients.spotify.dto.GetAccessTokenRequest;
import com.transfer.playlist.music.clients.spotify.service.SpotifyAuthService;

import jakarta.servlet.http.HttpSession;
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
    public ResponseEntity<Map<String, String>> getAccessToken(
        @Valid @RequestBody GetAccessTokenRequest request,
        HttpSession session
    ) {
        authService.getSpotifyAccessToken(request, session);
        return ResponseEntity.ok(Map.of("status", "success"));
    }
}
