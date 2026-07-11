package com.transfer.playlist.music.clients.spotify.dto.clientresponses;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SongDetails (
    @JsonProperty("name") String name,
    @JsonProperty("artists") List<Artist> artists,
    @JsonProperty("external_ids") Map<String, String> exIds
) {}
