package com.transfer.playlist.music.clients.youtube.dto.clientresponses;

import com.fasterxml.jackson.annotation.JsonProperty;

public record YoutubePlaylistContentDetails(
    @JsonProperty("itemCount") int itemCount
) {

}
