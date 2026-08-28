package com.transfer.playlist.music.clients.youtube.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.transfer.playlist.music.clients.common.dto.PlaylistSong;

class YouTubeMusicFilterTest {

    @Test
    void detectsTopicChannel() {
        assertTrue(YouTubeMusicFilter.isTopicChannel("Conan Gray - Topic"));
        assertTrue(YouTubeMusicFilter.isTopicChannel("  The Weeknd - Topic  "));
        assertFalse(YouTubeMusicFilter.isTopicChannel("Conan Gray"));
        assertFalse(YouTubeMusicFilter.isTopicChannel(null));
        assertFalse(YouTubeMusicFilter.isTopicChannel(""));
    }

    @Test
    void cleansTopicSuffixFromArtist() {
        assertEquals("Conan Gray", YouTubeMusicFilter.cleanArtist("Conan Gray - Topic"));
        assertEquals("John Mayer", YouTubeMusicFilter.cleanArtist("  John Mayer - Topic  "));
        assertEquals("Regular Artist", YouTubeMusicFilter.cleanArtist("Regular Artist"));
        assertEquals("", YouTubeMusicFilter.cleanArtist(null));
    }

    @Test
    void rejectsPlaceholderVideos() {
        assertTrue(YouTubeMusicFilter.isJunkItem(new PlaylistSong("Private video", "Some Channel", null)));
        assertTrue(YouTubeMusicFilter.isJunkItem(new PlaylistSong("private video", "Some Channel", null)));
    }

    @Test
    void rejectsBlankNameOrArtist() {
        assertTrue(YouTubeMusicFilter.isJunkItem(new PlaylistSong("", "Artist", null)));
        assertTrue(YouTubeMusicFilter.isJunkItem(new PlaylistSong("Song", "", null)));
        assertTrue(YouTubeMusicFilter.isJunkItem(new PlaylistSong("Song", "   ", null)));
    }

    @Test
    void rejectsNonMusicKeywordTitles() {
        assertTrue(YouTubeMusicFilter.isJunkItem(new PlaylistSong("Python for AI - Full Course", "Dave Ebbelaar", null)));
        assertTrue(YouTubeMusicFilter.isJunkItem(new PlaylistSong("Intro to Large Language Models", "Andrej Karpathy", null)));
        assertTrue(YouTubeMusicFilter.isJunkItem(new PlaylistSong("Complete RAG Crash Course", "Krish Naik", null)));
    }

    @Test
    void acceptsNormalMusicItems() {
        assertFalse(YouTubeMusicFilter.isJunkItem(new PlaylistSong("Heather", "Conan Gray - Topic", null)));
        assertFalse(YouTubeMusicFilter.isJunkItem(new PlaylistSong("After Hours", "The Weeknd - Topic", null)));
    }
}
