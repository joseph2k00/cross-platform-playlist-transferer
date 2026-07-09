package com.transfer.playlist.music.clients.spotify.dto.ClientResponses;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Song (
    @JsonProperty("item") SongDetails songDetails
) {}
