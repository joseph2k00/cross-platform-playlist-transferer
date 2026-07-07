package com.transfer.playlist.music.clients.spotify.dto.ClientResponses;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SpotifyPlaylistsApiResponse(
    @JsonProperty("next") String next,
    @JsonProperty("items") List<Playlists> list,
    @JsonProperty("total") int total
) {

}