package com.transfer.playlist.music.clients.spotify.dto.ClientResponses;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SpotifyPlaylistDetailsApiResponse(
    @JsonProperty("total") int total,
    @JsonProperty("items") List<Song> items,
    @JsonProperty("next") String next
) {}