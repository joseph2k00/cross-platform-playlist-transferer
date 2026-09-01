package com.transfer.playlist.music.clients.common.dto;

import java.util.List;

public record PlaylistBasicDetails(
    String id,
    String name,
    String desc,
    String img_url,
    List<PlaylistSong> songs
) {}
