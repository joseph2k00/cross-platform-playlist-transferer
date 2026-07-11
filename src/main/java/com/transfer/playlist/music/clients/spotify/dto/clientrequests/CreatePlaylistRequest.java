package com.transfer.playlist.music.clients.spotify.dto.clientrequests;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreatePlaylistRequest(
    String name,
    String description,
    @JsonProperty("public") boolean isPublic
) {}
