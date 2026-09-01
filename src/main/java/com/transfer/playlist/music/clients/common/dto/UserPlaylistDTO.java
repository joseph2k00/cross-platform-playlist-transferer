package com.transfer.playlist.music.clients.common.dto;

import java.util.List;

public record UserPlaylistDTO(
    int count,
    String source,
    List<PlaylistBasicDetails> playlists
) {}
