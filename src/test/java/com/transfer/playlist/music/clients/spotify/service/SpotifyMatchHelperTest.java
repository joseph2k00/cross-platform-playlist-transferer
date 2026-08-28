package com.transfer.playlist.music.clients.spotify.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.transfer.playlist.music.clients.spotify.dto.clientresponses.Artist;
import com.transfer.playlist.music.clients.spotify.dto.clientresponses.Track;

class SpotifyMatchHelperTest {

    private Track track(String name, String... artists) {
        return new Track(
            name,
            List.of(artists).stream().map(a -> new Artist(a)).toList(),
            "spotify:track:abc"
        );
    }

    @Test
    void matchesExactTitleAndArtist() {
        assertTrue(SpotifyMatchHelper.isMatch(track("Heather", "Conan Gray"), "Heather", "Conan Gray"));
    }

    @Test
    void matchesCaseInsensitively() {
        assertTrue(SpotifyMatchHelper.isMatch(track("heather", "conan gray"), "Heather", "Conan Gray"));
    }

    @Test
    void ignoresFeaturedArtistsInTitle() {
        assertTrue(SpotifyMatchHelper.isMatch(track("Beautiful People", "Ed Sheeran"), "Beautiful People (feat. Khalid)", "Ed Sheeran - Topic"));
    }

    @Test
    void rejectsWrongTitle() {
        assertFalse(SpotifyMatchHelper.isMatch(track("After Hours", "The Weeknd"), "The Hills", "The Weeknd"));
    }

    @Test
    void rejectsWrongArtist() {
        assertFalse(SpotifyMatchHelper.isMatch(track("Heather", "John Mayer"), "Heather", "Conan Gray"));
    }

    @Test
    void acceptsNullUriAsNonMatch() {
        Track noUri = new Track("Heather", List.of(new Artist("Conan Gray")), null);
        assertFalse(SpotifyMatchHelper.isMatch(noUri, "Heather", "Conan Gray"));
    }

    @Test
    void artistOverlapAllowsSubstringMatch() {
        assertTrue(SpotifyMatchHelper.isMatch(track("Apocalypse", "Cigarettes After Sex"), "Apocalypse", "Cigarettes After Sex - Topic"));
    }

    @Test
    void normalizesPunctuationInTitle() {
        assertTrue(SpotifyMatchHelper.isMatch(track("it's ok, you're ok", "bonjr"), "it's ok, you're ok", "bonjr"));
    }
}
