package com.transfer.playlist.music.clients.youtube.dto.clientresponses;

import com.fasterxml.jackson.annotation.JsonProperty;

public record YoutubeSearchItemSnippet(
    @JsonProperty("title") String title,
    @JsonProperty("channelTitle") String channelTitle
) {}
