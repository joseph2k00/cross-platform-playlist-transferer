package com.transfer.playlist.music.clients.youtube.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GetAccessTokenResponse(
    @JsonProperty("access_token") String accessToken,
    @JsonProperty("expires_in") Long expiresIn,
    @JsonProperty("token_type") String tokenType,
    @JsonProperty("scope") String scope
) {}
