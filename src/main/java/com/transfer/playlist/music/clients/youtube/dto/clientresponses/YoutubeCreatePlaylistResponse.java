package com.transfer.playlist.music.clients.youtube.dto.clientresponses;

import com.fasterxml.jackson.annotation.JsonProperty;

public record YoutubeCreatePlaylistResponse(
    @JsonProperty("id") String id
) {}
