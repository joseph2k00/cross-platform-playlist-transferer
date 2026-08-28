package com.transfer.playlist.music.clients.youtube.dto.auth;

import jakarta.validation.constraints.NotNull;

public record GetAccessTokenRequest(
    @NotNull(message = "Missing Code") String code
) {}
