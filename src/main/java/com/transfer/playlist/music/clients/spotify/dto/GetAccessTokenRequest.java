package com.transfer.playlist.music.clients.spotify.dto;

import jakarta.validation.constraints.NotNull;

public record GetAccessTokenRequest(
    @NotNull(message = "Missing Code")
    String code
) {}
