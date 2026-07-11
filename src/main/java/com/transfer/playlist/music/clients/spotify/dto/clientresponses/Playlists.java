package com.transfer.playlist.music.clients.spotify.dto.clientresponses;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Playlists(
    @JsonProperty("id") String id,
    @JsonProperty("images") List<ImageObject> images,
    @JsonProperty("name") String name,
    @JsonProperty("description") String description,
    @JsonProperty("owner") Owner owner
) {}