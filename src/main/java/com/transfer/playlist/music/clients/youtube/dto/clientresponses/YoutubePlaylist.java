package com.transfer.playlist.music.clients.youtube.dto.clientresponses;

import com.fasterxml.jackson.annotation.JsonProperty;

public record YoutubePlaylist(
    @JsonProperty("id") String id,
    @JsonProperty("snippet") YoutubePlaylistSnippet snippet,
    @JsonProperty("contentDetails") YoutubePlaylistContentDetails contentDetails
) {

    public String imageURL() {
        return snippet == null || snippet.thumbnails() == null ?
            "" :
            snippet.thumbnails().getBestUrl();
    }
}
