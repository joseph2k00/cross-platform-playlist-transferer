package com.transfer.playlist.music.clients.spotify.dto.clientrequests;

import java.util.List;

public record AddItemsToPlaylistRequest(
    List<String> uris
) {}
