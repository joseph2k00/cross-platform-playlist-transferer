package com.transfer.playlist.music.clients.spotify.dto.ClientRequests;

import java.util.List;

public record AddItemsToPlaylistRequest(
    List<String> uris
) {}
