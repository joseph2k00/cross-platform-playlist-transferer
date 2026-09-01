package com.transfer.playlist.music.clients.youtube.dto.clientresponses;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record YoutubePlaylistsApiResponse(
    @JsonProperty("nextPageToken") String next,
    @JsonProperty("items") List<YoutubePlaylist> list,
    @JsonProperty("pageInfo") YoutubePageInfo pageInfo
) {

    public int total() {
        return pageInfo == null ? 0 : pageInfo.totalResults();
    }
}
