package com.transfer.playlist.music.clients.spotify.dto.ClientResponses;

import java.util.List;

public record TrackSearch(
    List<Track> items
) {}
