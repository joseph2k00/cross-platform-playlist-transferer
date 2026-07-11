package com.transfer.playlist.music.clients.spotify.dto.ClientRequests;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreatePlaylistRequest(
    String name,
    String description,
    @JsonProperty("public") boolean isPublic
) {}
