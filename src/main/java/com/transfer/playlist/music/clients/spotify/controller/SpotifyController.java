package com.transfer.playlist.music.clients.spotify.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.transfer.playlist.music.clients.spotify.dto.GetAccessTokenRequest;
import com.transfer.playlist.music.clients.spotify.dto.GetUserPlaylistsResponse;
import com.transfer.playlist.music.clients.spotify.service.SpotifyApiService;
import com.transfer.playlist.music.clients.spotify.service.SpotifyAuthService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/spotify")
public class SpotifyController {

    private final SpotifyAuthService authService;
    private final SpotifyApiService apiService;

    public SpotifyController(
        SpotifyAuthService authService,
        SpotifyApiService apiService
    ) {
        this.authService = authService;
        this.apiService = apiService;
    }

    @PostMapping("/get-access-token")
    public ResponseEntity<Map<String, String>> getAccessToken(
        @Valid @RequestBody GetAccessTokenRequest request,
        HttpSession session
    ) {
        authService.getSpotifyAccessToken(request, session);
        return ResponseEntity.ok(Map.of("status", "success"));
    }

    @GetMapping("/playlists")
    public GetUserPlaylistsResponse getPlaylists(
        HttpSession session
    ) {
        String token = (String) session.getAttribute(SpotifyAuthService.SPOTIFY_ACCESS_TOKEN_SESSION_KEY);
        if (token == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not connected to Spotify");
        }

        return apiService.getUserPlaylists(token);
    }
}
