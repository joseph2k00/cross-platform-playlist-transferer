package com.transfer.playlist.music.clients.youtube.dto.clientresponses;

import com.fasterxml.jackson.annotation.JsonProperty;

public record YoutubePlaylistSnippet(
    @JsonProperty("title") String title,
    @JsonProperty("description") String description,
    @JsonProperty("thumbnails") YoutubeThumbnails thumbnails
) {

}
