package com.transfer.playlist.music.clients.spotify.dto.clientresponses;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Track(
    @JsonProperty("name") String name,
    @JsonProperty("artists") List<Artist> artists,
    @JsonProperty("uri") String uri
) {}
