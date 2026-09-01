package com.transfer.playlist.music.clients.youtube.dto.clientresponses;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record YoutubeSearchResponse(
    @JsonProperty("items") List<YoutubeSearchItem> items
) {}
