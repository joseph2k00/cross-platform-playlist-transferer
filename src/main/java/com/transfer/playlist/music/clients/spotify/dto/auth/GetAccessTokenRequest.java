package com.transfer.playlist.music.clients.spotify.dto.auth;
import jakarta.validation.constraints.NotNull;

public record GetAccessTokenRequest(
    @NotNull(message = "Missing Code") String code
) {}
