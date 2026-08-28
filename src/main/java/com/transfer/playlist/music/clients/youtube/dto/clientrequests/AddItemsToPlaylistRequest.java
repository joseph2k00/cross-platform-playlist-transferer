package com.transfer.playlist.music.clients.youtube.dto.clientrequests;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AddItemsToPlaylistRequest(
    @JsonProperty("snippet") Snippet snippet
) {
    public record Snippet(
        @JsonProperty("playlistId") String playlistId,
        @JsonProperty("resourceId") ResourceId resourceId
    ) {}

    public record ResourceId(
        @JsonProperty("kind") String kind,
        @JsonProperty("videoId") String videoId
    ) {}

    public static AddItemsToPlaylistRequest of(String playlistId, String videoId) {
        return new AddItemsToPlaylistRequest(
            new Snippet(
                playlistId,
                new ResourceId("youtube#video", videoId)
            )
        );
    }
}
