package com.transfer.playlist.music.clients.youtube.dto.clientresponses;

import com.fasterxml.jackson.annotation.JsonProperty;

public record YoutubeSearchId(
    @JsonProperty("videoId") String videoId
) {}
