package com.transfer.playlist.music.clients.spotify.dto.clientresponses;

import java.util.List;

public record TrackSearch(
    List<Track> items
) {}
