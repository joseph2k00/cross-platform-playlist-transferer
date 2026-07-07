package com.transfer.playlist.music.clients.spotify.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GetAccessTokenResponse(
    @JsonProperty("access_token") String accessToken
) {}
