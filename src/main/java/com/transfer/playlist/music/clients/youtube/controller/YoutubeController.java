package com.transfer.playlist.music.clients.youtube.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.transfer.playlist.music.clients.common.dto.UserPlaylistDTO;
import com.transfer.playlist.music.clients.youtube.dto.RedirectLinkResponse;
import com.transfer.playlist.music.clients.youtube.dto.auth.GetAccessTokenRequest;
import com.transfer.playlist.music.clients.youtube.service.AuthService;
import com.transfer.playlist.music.clients.youtube.service.YoutubeApiService;

import jakarta.servlet.http.HttpSession;
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
    public ResponseEntity<Map<String, String>> getAccessToken(
        @Valid @RequestBody GetAccessTokenRequest request,
        HttpSession session
    ) {
        authService.getYoutubeAccessToken(request, session);
        return ResponseEntity.ok(Map.of("status", "success"));
    }

    @GetMapping("/playlists")
    public UserPlaylistDTO getPlaylists(
        HttpSession session
    ) {
        String token = (String) session.getAttribute(YoutubeApiService.YOUTUBE_ACCESS_TOKEN_SESSION_KEY);
        if (token == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not connected to YouTube");
        }

        return apiService.getUserPlaylists(token);
    }
}
