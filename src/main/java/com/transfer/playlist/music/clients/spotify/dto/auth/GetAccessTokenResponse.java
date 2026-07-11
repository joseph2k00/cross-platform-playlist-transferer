package com.transfer.playlist.music.clients.spotify.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GetAccessTokenResponse(
    @JsonProperty("access_token") String accessToken
) {}
