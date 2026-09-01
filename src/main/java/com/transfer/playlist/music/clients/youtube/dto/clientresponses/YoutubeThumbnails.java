package com.transfer.playlist.music.clients.youtube.dto.clientresponses;

import com.fasterxml.jackson.annotation.JsonProperty;

public record YoutubeThumbnails(
    @JsonProperty("default") YoutubeThumbnail defaults,
    @JsonProperty("medium") YoutubeThumbnail medium,
    @JsonProperty("high") YoutubeThumbnail high
) {

    public String getBestUrl() {
        if (high != null && high.url() != null) return high.url();
        if (medium != null && medium.url() != null) return medium.url();
        if (defaults != null && defaults.url() != null) return defaults.url();
        return "";
    }
}
