package com.transfer.playlist.music.clients.spotify.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.transfer.playlist.music.clients.spotify.dto.clientresponses.Track;

final class SpotifyMatchHelper {

    private SpotifyMatchHelper() {}

    static boolean isMatch(Track candidate, String requestedName, String requestedArtist) {
        if (candidate == null || candidate.uri() == null) return false;

        String candName = normalize(candidate.name());
        String wantName = normalize(requestedName);
        if (candName.isEmpty() || wantName.isEmpty() || !candName.equals(wantName)) return false;

        List<String> candArtists = candidateArtists(candidate);
        return !candArtists.isEmpty() && artistsOverlap(candArtists, normalizeArtist(requestedArtist));
    }

    private static List<String> candidateArtists(Track candidate) {
        List<String> result = new ArrayList<>();
        if (candidate.artists() != null) {
            for (var artist: candidate.artists()) {
                if (artist != null && artist.name() != null) {
                    result.add(normalizeArtist(artist.name()));
                }
            }
        }
        return result;
    }

    private static boolean artistsOverlap(List<String> candidateArtists, String requestedArtist) {
        if (requestedArtist.isEmpty()) return true;

        String[] requestedTokens = requestedArtist.split("\\s+");
        for (String candidate: candidateArtists) {
            String[] candidateTokens = candidate.split("\\s+");
            if (tokenOverlap(requestedTokens, candidateTokens)) return true;
        }
        return false;
    }

    private static boolean tokenOverlap(String[] a, String[] b) {
        for (String ta: a) {
            if (ta.isEmpty()) continue;
            for (String tb: b) {
                if (tb.isEmpty()) continue;
                if (tb.equals(ta) || tb.contains(ta) || ta.contains(tb)) return true;
            }
        }
        return false;
    }

    static String normalize(String value) {
        if (value == null) return "";
        String s = value.toLowerCase(Locale.ROOT);
        s = s.replaceAll("\\(feat\\..*?\\)", "");
        s = s.replaceAll("\\(ft\\..*?\\)", "");
        s = s.replaceAll("[^a-z0-9 ]", " ");
        return s.replaceAll("\\s+", " ").trim();
    }

    static String normalizeArtist(String value) {
        if (value == null) return "";
        return value.toLowerCase(Locale.ROOT).replaceAll("\\s+", " ").trim();
    }
}
