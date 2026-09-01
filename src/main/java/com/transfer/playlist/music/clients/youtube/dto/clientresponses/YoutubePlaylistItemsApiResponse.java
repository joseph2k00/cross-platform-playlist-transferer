package com.transfer.playlist.music.clients.youtube.dto.clientresponses;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record YoutubePlaylistItemsApiResponse(
    @JsonProperty("nextPageToken") String next,
    @JsonProperty("items") List<YoutubePlaylistItem> items
) {

}
