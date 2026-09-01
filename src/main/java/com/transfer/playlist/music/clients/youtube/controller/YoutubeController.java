package com.transfer.playlist.music.clients.youtube.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.transfer.playlist.music.clients.common.dto.UserPlaylistDTO;
import com.transfer.playlist.music.clients.youtube.dto.RedirectLinkResponse;
import com.transfer.playlist.music.clients.youtube.dto.auth.GetAccessTokenRequest;
import com.transfer.playlist.music.clients.youtube.dto.auth.GetAccessTokenResponse;
import com.transfer.playlist.music.clients.youtube.service.AuthService;
import com.transfer.playlist.music.clients.youtube.service.YoutubeApiService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/youtube")
public class YoutubeController {

    private final AuthService authService;
    private final YoutubeApiService apiService;

    public YoutubeController(
        AuthService authService,
        YoutubeApiService apiService
    ) {
        this.authService = authService;
        this.apiService = apiService;
    }

    @GetMapping("/auth-link")
    public RedirectLinkResponse generateRedirectLink() {
        return authService.generateLink();
    }

    @PostMapping("/get-access-token")
    public GetAccessTokenResponse getAccessToken(
        @Valid @RequestBody GetAccessTokenRequest request
    ) {
        return authService.getYoutubeAccessToken(request);
    }

    @GetMapping("/playlists")
    public UserPlaylistDTO getPlaylists(
        @RequestHeader("Authorization") String authorization
    ) {
        String token = extractBearerToken(authorization);
        if (token == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not connected to YouTube");
        }

        return apiService.getUserPlaylists(token);
    }

    @PostMapping("/playlist/create")
    public ResponseEntity<Map<String, String>> createPlaylist(
        @RequestHeader("Authorization") String authorization,
        @Valid @RequestBody UserPlaylistDTO request
    ) {
        String token = extractBearerToken(authorization);
        if (token == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not connected to YouTube");
        }
        apiService.createPlaylistAndAddSongs(
            token,
            request
        );
        return ResponseEntity.ok(Map.of("status", "success"));
    }

    private String extractBearerToken(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return null;
        }
        return authorization.substring("Bearer ".length());
    }
}
