package com.transfer.playlist.music.clients.youtube.dto.clientrequests;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreatePlaylistRequest(
    @JsonProperty("snippet") Snippet snippet,
    @JsonProperty("status") Status status
) {
    public record Snippet(
        @JsonProperty("title") String title,
        @JsonProperty("description") String description
    ) {}

    public record Status(
        @JsonProperty("privacyStatus") String privacyStatus
    ) {}

    public static CreatePlaylistRequest of(String title, String description) {
        return new CreatePlaylistRequest(
            new Snippet(title, description == null ? "" : description),
            new Status("public")
        );
    }
}
