package com.transfer.playlist.music.clients.spotify.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.transfer.playlist.music.clients.spotify.dto.GetUserPlaylistsResponse;
import com.transfer.playlist.music.clients.spotify.dto.PlaylistBasicDetails;
import com.transfer.playlist.music.clients.spotify.dto.PlaylistSong;
import com.transfer.playlist.music.clients.spotify.dto.ClientResponses.SpotifyPlaylistsApiResponse;
import com.transfer.playlist.music.clients.spotify.dto.ClientResponses.Artist;
import com.transfer.playlist.music.clients.spotify.dto.ClientResponses.Playlists;
import com.transfer.playlist.music.clients.spotify.dto.ClientResponses.Song;
import com.transfer.playlist.music.clients.spotify.dto.ClientResponses.SongDetails;
import com.transfer.playlist.music.clients.spotify.dto.ClientResponses.SpotifyCurrentUserDetailsResponse;
import com.transfer.playlist.music.clients.spotify.dto.ClientResponses.SpotifyPlaylistDetailsApiResponse;

@Service
public class SpotifyApiService {

    private final RestClient restClient;
    private final static String SPOTIFY_API_BASE_URL = "https://api.spotify.com/v1";
    private final static String PLAYLISTS_URI = "/me/playlists";
    private final static String PLAYLIST_DETAILS_URI = "/playlists/{id}/items";
    private final static String USER_PROFILE_DETAILS_API = "/me";
    private final static String ISRC = "isrc";

    public SpotifyApiService(
        RestClient.Builder builder
    ) {
        this.restClient = builder
            .baseUrl(SPOTIFY_API_BASE_URL)
            .build();
    }

    public GetUserPlaylistsResponse getUserPlaylists(
        String accessToken
    ) {
        SpotifyCurrentUserDetailsResponse currentUser = getCurrentUserDetails(accessToken);

        SpotifyPlaylistsApiResponse response = callSpotifyPlaylistsAPI(
            PLAYLISTS_URI,
            accessToken
        );

        List<PlaylistBasicDetails> list = new ArrayList<>();
        int removed = 0;

        do {
            for (Playlists playlist: response.list()) {
                if (playlist.owner().id().compareTo(currentUser.id()) != 0) {
                    removed++;
                    continue;
                }
                String imageURL = playlist.images().get(0).url();
                List<PlaylistSong> songs = getAllSongsInPlaylist(playlist.id(), accessToken);
                PlaylistBasicDetails pl = new PlaylistBasicDetails(
                    playlist.id(),
                    playlist.name(),
                    playlist.description(),
                    imageURL,
                    songs
                );
                list.add(pl);
            }
            
            if (response.next() == null) break;
            response = callSpotifyPlaylistsAPI(
                response.next(),
                accessToken
            );

        } while (true);

        return new GetUserPlaylistsResponse(
            response.total() - removed,
            list
        );
    }

    private List<PlaylistSong> getAllSongsInPlaylist(String playlistId, String accessToken) {
        List<PlaylistSong> songs = new ArrayList<>();
        SpotifyPlaylistDetailsApiResponse response = callSpotifyPlaylistAPI(playlistId, accessToken);
        do {
            for (Song song: response.items()) {
                SongDetails details = song.songDetails();
                String artist = "";
                for (Artist a: details.artists()) {
                    artist += a.name() + " ";
                }
                songs.add(
                    new PlaylistSong(
                        details.name(),
                        artist,
                        details.exIds().getOrDefault(ISRC, null)
                    )
                );
            }
            if (response.next() == null) break;
            response = callNextSpotifyPlaylistAPI(response.next(), accessToken);
        } while(true);
        return songs;
    }

    private SpotifyPlaylistDetailsApiResponse callSpotifyPlaylistAPI(String playlistId, String accessToken) {
        return restClient.get()
            .uri(PLAYLIST_DETAILS_URI, playlistId)
            .header("Authorization", "Bearer " + accessToken)
            .retrieve()
            .body(SpotifyPlaylistDetailsApiResponse.class);
    }
    
    private SpotifyPlaylistDetailsApiResponse callNextSpotifyPlaylistAPI(String nextUri, String accessToken) {
        return restClient.get()
            .uri(nextUri)
            .header("Authorization", "Bearer " + accessToken)
            .retrieve()
            .body(SpotifyPlaylistDetailsApiResponse.class);
    }

    private SpotifyPlaylistsApiResponse callSpotifyPlaylistsAPI(String uri, String accessToken) {
        return restClient.get()
            .uri(uri)
            .header("Authorization", "Bearer " + accessToken)
            .retrieve()
            .body(SpotifyPlaylistsApiResponse.class);
    }

    private SpotifyCurrentUserDetailsResponse getCurrentUserDetails(String accessToken) {
        return restClient.get()
            .uri(USER_PROFILE_DETAILS_API)
            .header("Authorization", "Bearer " + accessToken)
            .retrieve()
            .body(SpotifyCurrentUserDetailsResponse.class);
    } 
}
