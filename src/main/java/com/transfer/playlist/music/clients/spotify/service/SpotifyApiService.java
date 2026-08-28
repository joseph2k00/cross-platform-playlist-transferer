package com.transfer.playlist.music.clients.spotify.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.transfer.playlist.music.clients.spotify.dto.clientrequests.AddItemsToPlaylistRequest;
import com.transfer.playlist.music.clients.spotify.dto.clientrequests.CreatePlaylistRequest;
import com.transfer.playlist.music.clients.spotify.dto.clientresponses.SpotifyPlaylistsApiResponse;
import com.transfer.playlist.music.clients.spotify.dto.clientresponses.SpotifySearchTrackResponse;
import com.transfer.playlist.music.clients.spotify.dto.clientresponses.SpotifyCreatePlaylistResponse;
import com.transfer.playlist.music.clients.spotify.exception.SpotifyAuthException;
import com.transfer.playlist.music.clients.common.dto.PlaylistBasicDetails;
import com.transfer.playlist.music.clients.common.dto.PlaylistSong;
import com.transfer.playlist.music.clients.common.dto.UserPlaylistDTO;
import com.transfer.playlist.music.clients.spotify.dto.clientresponses.Artist;
import com.transfer.playlist.music.clients.spotify.dto.clientresponses.Playlists;
import com.transfer.playlist.music.clients.spotify.dto.clientresponses.Song;
import com.transfer.playlist.music.clients.spotify.dto.clientresponses.SongDetails;
import com.transfer.playlist.music.clients.spotify.dto.clientresponses.SpotifyCurrentUserDetailsResponse;
import com.transfer.playlist.music.clients.spotify.dto.clientresponses.SpotifyPlaylistDetailsApiResponse;
import com.transfer.playlist.music.clients.spotify.dto.clientresponses.Track;

@Service
public class SpotifyApiService {

    private final RestClient restClient;
    private final static String SPOTIFY_API_BASE_URL = "https://api.spotify.com/v1";
    private final static String PLAYLISTS_URI = "/me/playlists";
    private final static String PLAYLIST_DETAILS_URI = "/playlists/{id}/items";
    private final static String ADD_ITEM_TO_PLAYLIST_URI = "/playlists/{id}/items";
    private final static String USER_PROFILE_DETAILS_API = "/me";
    private final static String CREATE_PLAYLIST_URI = "/me/playlists";
    private final static String SEARCH_API = "/search";
    private final static String ISRC = "isrc";
    private final static String SOURCE = "Spotify";

    public SpotifyApiService(
        RestClient.Builder builder
    ) {
        this.restClient = builder
            .baseUrl(SPOTIFY_API_BASE_URL)
            .build();
    }

    public UserPlaylistDTO getUserPlaylists(String accessToken) {
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
                String imageURL = playlist.images() == null ?
                    "":
                    playlist.images().get(0).url();
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

        return new UserPlaylistDTO(
            response.total() - removed,
            SOURCE,
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

    public void createPlaylistAndAddSongs(
        String accessToken,
        UserPlaylistDTO request
    ) {
        for (PlaylistBasicDetails playlist: request.playlists()) {
            String playlistId = createPlaylist(playlist.name(), accessToken);
            addSongsToPlaylist(playlistId, playlist.songs(), accessToken);
        }
    }

    public String createPlaylist(
        String name,
        String accessToken
    ) {
        CreatePlaylistRequest requestBody = new CreatePlaylistRequest(
            name,
            "Transferred from my app",
            false
        );

        SpotifyCreatePlaylistResponse response = restClient.post()
            .uri(CREATE_PLAYLIST_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .headers(headers -> headers.setBearerAuth(accessToken))
            .body(requestBody)
            .retrieve()
            .onStatus(status -> status.isError(), (req, res) -> {
                throw new SpotifyAuthException(
                    new String(res.getBody().readAllBytes()),
                    res.getStatusCode()
                );
            })
            .body(SpotifyCreatePlaylistResponse.class);

        return response.id();
    }

    public void addSongsToPlaylist(
        String playlistId,
        List<PlaylistSong> songs,
        String accessToken
    ) {
        List<String> uris = new ArrayList<>();

        for (PlaylistSong song: songs) {
            String uri = getMatchingSongUri(song, accessToken);
            if (uri != null) {
                uris.add(uri);
            }
        }

        AddItemsToPlaylistRequest requestBody = new AddItemsToPlaylistRequest(uris);

        restClient.post()
            .uri(ADD_ITEM_TO_PLAYLIST_URI, playlistId)
            .contentType(MediaType.APPLICATION_JSON)
            .headers(headers -> headers.setBearerAuth(accessToken))
            .body(requestBody)
            .retrieve()
            .onStatus(status -> status.isError(), (req, res) -> {
                throw new SpotifyAuthException(
                    new String(res.getBody().readAllBytes()),
                    res.getStatusCode()
                );
            })
            .toBodilessEntity();
    }

    public String getMatchingSongUri(PlaylistSong song, String accessToken) {
        String requestedName = song.name() == null ? "" : song.name();
        String requestedArtist = song.artist() == null ? "" : song.artist();

        SpotifySearchTrackResponse response = restClient.get()
            .uri(uriBuilder -> uriBuilder
                    .path(SEARCH_API)
                    .queryParam("type", "track")
                    .queryParam("q", requestedName + " " + requestedArtist)
                    .build())
            .headers(headers -> headers.setBearerAuth(accessToken))
            .retrieve()
            .body(SpotifySearchTrackResponse.class);

        if (response == null || response.tracks() == null || response.tracks().items() == null) {
            return null;
        }

        for (Track track: response.tracks().items()) {
            if (SpotifyMatchHelper.isMatch(track, requestedName, requestedArtist)) {
                return track.uri();
            }
        }
        return null;
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
