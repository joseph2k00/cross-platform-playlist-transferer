package com.transfer.playlist.music.clients.youtube.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.transfer.playlist.music.clients.youtube.dto.RedirectLinkResponse;
import com.transfer.playlist.music.clients.youtube.service.AuthService;

@RestController
@RequestMapping("/youtube")
public class YoutubeController {

    private final AuthService authService;

    public YoutubeController(
        AuthService authService
    ) {
        this.authService = authService;
    }

    @GetMapping("/auth-link")
    public RedirectLinkResponse generateRedirectLink() {
        return authService.generateLink();
    }
}
