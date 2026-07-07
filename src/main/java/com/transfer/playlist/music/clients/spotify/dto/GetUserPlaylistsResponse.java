package com.transfer.playlist.music.clients.spotify.dto;

import java.util.List;

public record GetUserPlaylistsResponse(
    int count,
    List<PlaylistBasicDetails> playlists
) {

}
