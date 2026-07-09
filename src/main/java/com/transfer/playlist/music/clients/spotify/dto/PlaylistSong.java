package com.transfer.playlist.music.clients.spotify.dto;

public record PlaylistSong(
    String name,
    String artist,
    String isrc
) {}
