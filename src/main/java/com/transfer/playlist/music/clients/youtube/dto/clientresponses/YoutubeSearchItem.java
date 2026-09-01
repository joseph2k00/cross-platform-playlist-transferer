package com.transfer.playlist.music.clients.youtube.dto.clientresponses;

import com.fasterxml.jackson.annotation.JsonProperty;

public record YoutubeSearchItem(
    @JsonProperty("id") YoutubeSearchId id,
    @JsonProperty("snippet") YoutubeSearchItemSnippet snippet
) {}
