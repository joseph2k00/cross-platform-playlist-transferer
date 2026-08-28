package com.transfer.playlist.music.clients.youtube.dto.clientresponses;

import com.fasterxml.jackson.annotation.JsonProperty;

public record YoutubePlaylistItemSnippet(
    @JsonProperty("title") String title,
    @JsonProperty("videoOwnerChannelTitle") String videoOwnerChannelTitle,
    @JsonProperty("resourceId") YoutubeResourceId resourceId
) {

    public String songName() {
        return title == null ? "" : title;
    }

    public String artist() {
        return videoOwnerChannelTitle == null ? "" : videoOwnerChannelTitle;
    }
}
