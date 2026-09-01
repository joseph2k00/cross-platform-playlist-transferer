package com.transfer.playlist.music.clients.youtube.service;

import java.util.Locale;
import java.util.Set;

import com.transfer.playlist.music.clients.common.dto.PlaylistSong;

public final class YouTubeMusicFilter {

    private static final String TOPIC_SUFFIX = "- topic";
    private static final Set<String> PLACEHOLDER_TITLES = Set.of(
        "private video",
        "deleted video",
        "[private video]"
    );
    private static final Set<String> NON_MUSIC_KEYWORDS = Set.of(
        "course",
        "tutorial",
        "lecture",
        "crash course",
        "roadmap",
        "intro to",
        "talk",
        "podcast",
        "vlog",
        "episode",
        "review",
        "#shorts"
    );

    private YouTubeMusicFilter() {}

    public static boolean isTopicChannel(String artist) {
        if (artist == null) return false;
        return artist.trim().toLowerCase(Locale.ROOT).endsWith(TOPIC_SUFFIX);
    }

    public static String cleanArtist(String artist) {
        if (!isTopicChannel(artist)) return artist == null ? "" : artist.trim();
        String trimmed = artist.trim();
        return trimmed.substring(0, trimmed.length() - TOPIC_SUFFIX.length()).trim();
    }

    public static boolean isJunkItem(PlaylistSong song) {
        if (song == null) return true;
        String name = song.name() == null ? "" : song.name().trim();
        String artist = song.artist() == null ? "" : song.artist().trim();

        if (name.isEmpty() || artist.isEmpty()) return true;

        String lower = name.toLowerCase(Locale.ROOT);
        if (PLACEHOLDER_TITLES.contains(lower)) return true;

        for (String keyword: NON_MUSIC_KEYWORDS) {
            if (lower.contains(keyword)) return true;
        }
        return false;
    }
}
