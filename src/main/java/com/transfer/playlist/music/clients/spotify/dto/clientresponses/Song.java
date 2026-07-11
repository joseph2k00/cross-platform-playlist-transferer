package com.transfer.playlist.music.clients.spotify.dto.clientresponses;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Song (
    @JsonProperty("item") SongDetails songDetails
) {}
